package com.wis.security.handler;

import com.wis.security.pojo.WisSecurityConfigurationProperty;
import com.wis.security.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liuBo
 * 2019/12/16.
 */
@Slf4j
public class DefaultJwtLoginSuccessHandler extends LoginSuccessHandler {
    @Autowired
    private WisSecurityConfigurationProperty wisSecurityConfigurationProperty;

    public void onLogonSuccess(HttpServletRequest request, HttpServletResponse response,
                                  Authentication authentication){
        String authName = authentication.getName();
        if (authName != null) {
            //验证通过的处理
            String token = JwtUtils.createToken(wisSecurityConfigurationProperty.getJwtSecret(),wisSecurityConfigurationProperty.getJwtExpireSecond(),null,authName);
            response.addHeader("token", "Bearer " + token);
        }
    }


}
