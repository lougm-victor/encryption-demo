package com.charon.encryption.filter;

import com.alibaba.fastjson2.JSON;
import com.charon.encryption.constant.CommonConstants;
import com.charon.encryption.request.HttpRequestWrapper;
import com.charon.encryption.service.SecurityService;
import com.charon.encryption.utils.AESKey;
import com.charon.encryption.utils.AESUtils;
import com.charon.encryption.utils.RSAUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 数据解密过滤器
 *
 * @author charon
 * @date 2025/3/31 18:14
 */
@Component
public class DecryptionFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(DecryptionFilter.class);

    private final SecurityService securityService;

    private final RSAUtils rsaUtils;

    public DecryptionFilter(SecurityService securityService, RSAUtils rsaUtils) {
        this.securityService = securityService;
        this.rsaUtils = rsaUtils;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!requiresDecryption(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        String encryptedKey = httpRequest.getHeader(CommonConstants.ENCRYPTED_KEY);
        String signature = httpRequest.getHeader(CommonConstants.SIGNATURE);
        String timestamp = httpRequest.getHeader(CommonConstants.TIMESTAMP);
        String nonce = httpRequest.getHeader(CommonConstants.NONCE);

        if (StringUtils.isAnyBlank(encryptedKey, signature, timestamp, nonce)) {
            sendErrorResponse(httpResponse, "请求头参数缺失", HttpStatus.BAD_REQUEST);
            return;
        }

        try {
            if (!securityService.validateTimestamp(Long.parseLong(timestamp))) {
                sendErrorResponse(httpResponse, "请求已过期", HttpStatus.BAD_REQUEST);
                return;
            }

            if (!securityService.validateNonce(nonce)) {
                sendErrorResponse(httpResponse, "重复的请求", HttpStatus.BAD_REQUEST);
                return;
            }

            HttpRequestWrapper requestWrapper = new HttpRequestWrapper(httpRequest);
            Map<String, Object> requestBodyMap = JSON.parseObject(requestWrapper.getBody(), Map.class);
            String encryptedData = (String) requestBodyMap.get("data");

            if (StringUtils.isBlank(encryptedData)) {
                chain.doFilter(request, response);
            }

            String aesKeyJson = rsaUtils.decryptByBlock(encryptedKey);
            AESKey aesKey = JSON.parseObject(aesKeyJson, AESKey.class);

            String decryptedData;
            try {
                log.info("获取到加密数据: \n{}", encryptedData);
                decryptedData = AESUtils.decrypt(encryptedData, aesKey);
                log.info("解密后的数据: \n{}", decryptedData);
            } catch (Exception e) {
                log.error("数据解密失败: {}", e.getMessage(), e);
                sendErrorResponse(httpResponse, "内部服务器错误", HttpStatus.INTERNAL_SERVER_ERROR);
                return;
            }

            Map<String, Object> params = JSON.parseObject(decryptedData, Map.class);

            if (!securityService.verifySignature(params, timestamp, nonce, aesKey.getKey(), signature)) {
                sendErrorResponse(httpResponse, "数据签名验证失败", HttpStatus.BAD_REQUEST);
                return;
            }
            log.info("数据签名验证通过!");

            requestWrapper.setBody(decryptedData);

            chain.doFilter(requestWrapper, response);
        } catch (Exception e) {
            sendErrorResponse(httpResponse, "数据解密失败", HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * 判断请求是否需要解密处理
     */
    private boolean requiresDecryption(HttpServletRequest request) {
        // 1. 只处理POST/PUT/PATCH请求
        String method = request.getMethod().toUpperCase();
        if (!"POST".equals(method) && !"PUT".equals(method) && !"PATCH".equals(method)) {
            return false;
        }

        // 2. 检查Content-Type
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE)) {
            return false;
        }

        // 3. 只处理特定路径的请求
        String uri = request.getRequestURI();
        return uri.startsWith("/userData");
    }

    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format("{\"msg\":\"%s\",\"code\":%d}", message, status.value())
        );
    }
}
