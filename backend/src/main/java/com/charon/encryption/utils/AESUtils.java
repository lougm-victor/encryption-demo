package com.charon.encryption.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES加解密工具类
 *
 * @author charon
 * @date 2025/3/31 19:47
 */
public class AESUtils {

    private static final String KEY_ALGORITHM = "AES";

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * AES密钥长度(16字节对应128位)
     */
    private static final int AES_KEY_SIZE = 16;

    /**
     * 随机向量长度(固定为16字节)
     */
    private static final int IV_SIZE = 16;

    /**
     * 参数按"加密算法/模式/填充模式" 。
     * <p>
     * (1)加密算法有：AES
     * <p>
     * (2) 模式有CBC(有向量模式)和ECB(无向量模式)等，使用CBC模式需要定义一个IvParameterSpec对象，使用CBC模式更加安全，一般建议使用CBC模式
     * <p>
     * (3) 填充模式:NoPadding、PKCS5Padding、PKCS7Padding等
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";


    public static void main(String[] args) {
        try {
            AESKey aesKey = generateAESKeyPair();
            System.out.println("aesKey.getKey() = " + aesKey.getKey());
            System.out.println("aesKey.getIv() = " + aesKey.getIv());

            String data = "{\"USER_ID\":\"001\",\"USER_NAME\":\"张三\",\"AGE\":18,\"MOBILE\":\"16677889900\"}";
            String encryptData = encrypt(data, aesKey);
            System.out.println("encryptData = " + encryptData);

            String decryptData = decrypt(encryptData, aesKey);
            System.out.println("decryptData = " + decryptData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成 AES 密钥（Base64 编码）
     */
    public static String generateAESKey(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            key.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return Base64.getEncoder().encodeToString(key.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 IV（Base64 编码）
     */
    public static String generateIV(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder iv = new StringBuilder();
        for (int i = 0; i < length; i++) {
            iv.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return Base64.getEncoder().encodeToString(iv.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 AESKey 实例，包含 Base64 编码的密钥和 IV
     */
    public static AESKey generateAESKeyPair() {
        AESKey aesKey = new AESKey();
        aesKey.setKey(generateAESKey(AES_KEY_SIZE));
        aesKey.setIv(generateIV(IV_SIZE));
        return aesKey;
    }

    /**
     * 使用AES加密数据
     *
     * @param decryptedData 明文
     * @param aesKey        密钥与随机向量
     * @return 加密后的密文
     */
    public static String encrypt(String decryptedData, AESKey aesKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(aesKey.getKey());
        byte[] ivBytes = Base64.getDecoder().decode(aesKey.getIv());

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] encryptedBytes = cipher.doFinal(decryptedData.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 数据解密
     *
     * @param encryptedData 加密后的密文
     * @param aesKey        密钥与随机向量
     * @return 解密后的明文
     */
    public static String decrypt(String encryptedData, AESKey aesKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(aesKey.getKey());
        byte[] ivBytes = Base64.getDecoder().decode(aesKey.getIv());

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
