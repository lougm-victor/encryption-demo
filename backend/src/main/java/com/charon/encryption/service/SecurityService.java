package com.charon.encryption.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 安全服务类
 *
 * @author charon
 * @date 2025/3/31 19:32
 */
@Service
public class SecurityService {

    @Value("${security.timestamp.tolerance:5}")
    private long timestampTolerance;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 验证时间戳是否在有效范围内
     */
    public boolean validateTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        return Math.abs(currentTime - timestamp) <= TimeUnit.MINUTES.toMillis(timestampTolerance);
    }

    /**
     * 验证nonce是否已使用过
     */
    public boolean validateNonce(String nonce) {
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(
                "nonce:" + nonce,
                "1",
                timestampTolerance,
                TimeUnit.MINUTES
        );
        return success != null && success;
    }

    /**
     * 接口验签
     *
     * @param params    请求体参数
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @param aesKey    AES密钥
     * @param signature 签名
     * @return true / false
     */
    public boolean verifySignature(Map<String, Object> params, String timestamp,
                                   String nonce, String aesKey, String signature) {

        // 1. 请求体参数排序
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);
        Map<String, Object> sortedParams = new LinkedHashMap<>();
        for (String key : sortedKeys) {
            sortedParams.put(key, params.get(key));
        }
        String paramStr = JSON.toJSONString(sortedParams, SerializerFeature.WriteMapNullValue);

        // 2. 组合签名字符串
        String[] parts = {
                "params=" + paramStr,
                "timestamp=" + timestamp,
                "nonce=" + nonce,
                "aesKey=" + aesKey,
                "version=1.0"
        };
        Arrays.sort(parts);
        String signStr = String.join("&", parts);

        // 3. 多重哈希
        String hash = DigestUtils.sha512Hex(signStr);
        hash = DigestUtils.sha256Hex(hash);
        hash = DigestUtils.md5Hex(hash);

        // 4. 签名比对
        return hash.equals(signature);
    }
}
