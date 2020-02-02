package com.wis.security.pojo;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 验证码和答案封装信息
 * Created by liuBo
 * 2020/1/13.
 */
@Component
public class CaptchaAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, CaptchaAuthenticationDetails> {

    @Override
    public CaptchaAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new CaptchaAuthenticationDetails(context);
    }
}
