package com.charon.encryption.config;

import com.charon.encryption.filter.DecryptionFilter;
import com.charon.encryption.service.SecurityService;
import com.charon.encryption.utils.RSAUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 过滤器注册
 *
 * @author charon
 * @date 2025/3/31 19:55
 */
@Configuration
public class FilterConfig {

    @Resource
    private SecurityService securityService;

    @Resource
    private RSAUtils rsaUtils;

    @Bean
    public FilterRegistrationBean<DecryptionFilter> decryptionFilterRegistration() {
        FilterRegistrationBean<DecryptionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new DecryptionFilter(securityService, rsaUtils));
        registration.addUrlPatterns("/userData/*");
        registration.setOrder(1);
        return registration;
    }
}
