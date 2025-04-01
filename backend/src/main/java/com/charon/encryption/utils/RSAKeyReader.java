package com.charon.encryption.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * RSK密钥对读取
 *
 * @author charon
 * @date 2025/3/31 20:34
 */
@Component
public class RSAKeyReader {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public RSAKeyReader(@Value("${rsa.public-key-path}") Resource publicKeyResource,
                        @Value("${rsa.private-key-path}") Resource privateKeyResource) {
        try {
            // 读取公钥
            String publicKeyPem = new BufferedReader(new InputStreamReader(publicKeyResource.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));
            this.publicKey = convertStringToPublicKey(publicKeyPem);

            // 读取私钥
            String privateKeyPem = new BufferedReader(new InputStreamReader(privateKeyResource.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));
            this.privateKey = convertStringToPrivateKey(privateKeyPem);
        } catch (Exception e) {
            throw new RuntimeException("加载RSA密钥对失败", e);
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * 将PEM格式的公钥字符串转换为PublicKey对象
     *
     * @param publicKeyPEM 公钥字符串
     * @return 公钥对象
     */
    private PublicKey convertStringToPublicKey(String publicKeyPEM) throws Exception {
        // 移除PEM头尾和换行符
        String publicKeyContent = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 将PEM格式的私钥字符串转换为PrivateKey对象
     *
     * @param privateKeyPEM 私钥字符串
     * @return 私钥对象
     */
    private PrivateKey convertStringToPrivateKey(String privateKeyPEM) throws Exception {
        // 移除PEM头尾和换行符
        String privateKeyContent = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(keySpec);
    }
}
