package com.charon.encryption.utils;

/**
 * AES密钥
 *
 * @author charon
 * @date 2025/3/31 19:48
 */
public class AESKey {

    private String key;

    private String iv;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}
