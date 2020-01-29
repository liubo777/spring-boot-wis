package com.wis.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liuBo
 * 2019/12/16.
 */
@Slf4j
public class LoginErrorHandler implements AuthenticationFailureHandler {
    private String defaultFailureUrl = "/login";
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        if ("Captcha does not match.".equals(exception.getMessage())){
            request.setAttribute("error","code");
        }else{
            request.setAttribute("error","info");
        }
        request.getRequestDispatcher(defaultFailureUrl)
                .forward(request, response);

    }


}
