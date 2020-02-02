package com.wis.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liuBo
 * 2019/12/16.
 */
@Slf4j
public class DefaultLoginSuccessHandler extends LoginSuccessHandler {


    public void onLogonSuccess(HttpServletRequest request, HttpServletResponse response,
                                  Authentication authentication){
        String authName = authentication.getName();
        if (authName != null) {
            //验证通过的处理
            request.getSession().setAttribute("loginName", authName);
        }
    }


}
