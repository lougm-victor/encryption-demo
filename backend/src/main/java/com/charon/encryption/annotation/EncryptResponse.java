package com.charon.encryption.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加密注解
 *
 * @author charon
 * @date 2025/3/31 22:06
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptResponse {

    /**
     * 加密算法(可扩展其他算法)
     */
    String algorithm() default "AES";

    /**
     * 是否启用加密
     */
    boolean enabled() default true;
}
