package com.wis.security.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置参数类
 * Created by liuBo
 * 2019/12/9.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WisSecurityConfigurationProperty {
    @Builder.Default
    String defaultLoginUrl = "/login";
    @Builder.Default
    String mode = "simple";
    @Builder.Default
    Integer maxSessionNum = 10;
    @Builder.Default
    Boolean captcha = true;
    @Builder.Default
    Boolean session = true;
    @Builder.Default
    String errorPage="/500";
    @Builder.Default
    Boolean csrfEnabled = true;
    //不验证csrf的url
    List<String> csrfIgnore;
    //不经过spring security 验证
    List<String> securityIgnore;

    @Builder.Default
    Boolean corsEnabled = false;
    @Builder.Default
    List<String> corsMethods = new ArrayList(){{add("GET");add("POST");}};
    @Builder.Default
    String corsPattern = "/api/**";
    @Builder.Default
    List<String> corsOrigins = new ArrayList(){{add("*");}};
    String jwtSecret;
    @Builder.Default
    Integer jwtExpireSecond = 3600;
    //jwt过滤器校验的url
    List<String> jwtApprove = new ArrayList(){{add("/api/**");}};;

}


