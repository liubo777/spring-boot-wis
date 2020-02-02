package com.wis.security.pojo;

import javax.servlet.http.HttpServletRequest;

/**
 * 存储用来校验的验证码和答案
 * Created by liuBo
 * 2020/1/10.
 */
public class CaptchaAuthenticationDetails {
    private final String answer;
    private final String captcha;

    /**
     * @param request
     */
    public CaptchaAuthenticationDetails(HttpServletRequest request) {
        this.answer = request.getParameter("captcha");
        this.captcha = (String) request.getSession(true).getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
    }

    public String getAnswer() {
        return answer;
    }

    public String getCaptcha() {
        return captcha;
    }
}
