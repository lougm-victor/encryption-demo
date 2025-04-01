package com.charon.encryption.aspect;

import com.alibaba.fastjson.JSON;
import com.charon.encryption.annotation.EncryptResponse;
import com.charon.encryption.constant.CommonConstants;
import com.charon.encryption.reponse.AjaxResult;
import com.charon.encryption.utils.AESKey;
import com.charon.encryption.utils.AESUtils;
import com.charon.encryption.utils.RSAUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;

/**
 * 响应加密切面
 *
 * @author charon
 * @date 2025/3/31 22:07
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class EncryptResponseAdvice implements ResponseBodyAdvice<Object> {

    private final Logger log = LoggerFactory.getLogger(EncryptResponseAdvice.class);

    @Resource
    private RSAUtils rsaUtils;

    /**
     * 只处理带有@EncryptResponse注解的方法
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(EncryptResponse.class)
                || returnType.getContainingClass().isAnnotationPresent(EncryptResponse.class);
    }

    /**
     * 实际加密处理
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        EncryptResponse annotation = returnType.getMethodAnnotation(EncryptResponse.class);
        boolean enabled = annotation != null ? annotation.enabled() : Boolean.TRUE;

        if (!enabled) {
            return body;
        }

        AESKey aesKey = AESUtils.generateAESKeyPair();
        try {
            if (body instanceof AjaxResult) {
                String encryptData = AESUtils.encrypt(JSON.toJSONString(((AjaxResult) body).get(AjaxResult.DATA_TAG)), aesKey);

                String encryptedKey = rsaUtils.encryptByBlock(JSON.toJSONString(aesKey));
                response.getHeaders().add(CommonConstants.ENCRYPTED_KEY, encryptedKey);
                AjaxResult ajaxResult = new AjaxResult(Integer.parseInt(((AjaxResult) body).get(AjaxResult.CODE_TAG).toString()), ((AjaxResult) body).get(AjaxResult.MSG_TAG).toString(), encryptData);

                return ajaxResult;
            } else {
                return body;
            }
        } catch (Exception e) {
            log.error("AES encrypt error", e);
            return body;
        }
    }
}
