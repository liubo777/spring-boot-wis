package com.wis.security.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.wis.security.pojo.CaptchaAuthenticationDetailsSource;
import com.wis.security.pojo.WisSecurityConfigurationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Properties;

/**
 * Created by liuBo
 * 2019/12/6.
 */
@Configuration
public class SimpleConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "wis.security")
    public WisSecurityConfigurationProperty wisSecurityConfigurationProperty(){
        return WisSecurityConfigurationProperty.builder().build();
    }
    @Bean
    @ConditionalOnExpression("'${wis.security.cors-approve[0]:}'.length()>0 ")
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        WisSecurityConfigurationProperty wisSecurityConfigurationProperty = wisSecurityConfigurationProperty();
        configuration.setAllowedOrigins(wisSecurityConfigurationProperty.getCorsApprove());
        configuration.setAllowedMethods(wisSecurityConfigurationProperty.getCorsMethods());
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(wisSecurityConfigurationProperty.getCorsPattern(),configuration);
        return source;
    }

}
