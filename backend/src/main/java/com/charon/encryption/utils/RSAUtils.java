package com.charon.encryption.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * RSA加解密工具类
 *
 * @author lougm
 * @date 2025/3/31 19:4
 */
@Component
public class RSAUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSAUtils.class);

    private static final int MAX_BLOCK_SIZE = 245;

    private static final String SEPARATOR = "@~@";

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    @Autowired
    public RSAUtils(RSAKeyReader rsaKeyReader) {
        this.publicKey = rsaKeyReader.getPublicKey();
        this.privateKey = rsaKeyReader.getPrivateKey();
    }

    public String encryptByBlock(String data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] dataArray = URLEncoder.encode(data, String.valueOf(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);

            // 分段加密
            List<String> encryptedChunks = new ArrayList<>();
            for (int i = 0; i < dataArray.length; i += MAX_BLOCK_SIZE) {
                int endIndex = Math.min(i + MAX_BLOCK_SIZE, dataArray.length);
                byte[] tempData = new byte[endIndex - i];
                System.arraycopy(dataArray, i, tempData, 0, tempData.length);

                byte[] encryptedBlock = cipher.doFinal(tempData);
                encryptedChunks.add(Base64.getEncoder().encodeToString(encryptedBlock));
            }

            return String.join(SEPARATOR, encryptedChunks);
        } catch (Exception e) {
            LOGGER.error("数据加密失败: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public String decryptByBlock(String data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            String[] encryptedChunks = data.split(SEPARATOR);

            // 分段解密
            StringBuilder decryptedData = new StringBuilder();
            for (String chunk : encryptedChunks) {
                byte[] encryptedBytes = Base64.getDecoder().decode(chunk);

                byte[] decryptedChunk = cipher.doFinal(encryptedBytes);

                decryptedData.append(new String(decryptedChunk, StandardCharsets.UTF_8));
            }

            return URLDecoder.decode(decryptedData.toString(), String.valueOf(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOGGER.error("数据解密失败: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
