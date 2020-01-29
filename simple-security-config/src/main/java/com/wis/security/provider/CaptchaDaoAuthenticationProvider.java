package com.wis.security.provider;

import com.wis.security.pojo.CaptchaAuthenticationDetails;
import com.wis.security.pojo.WisSecurityConfigurationProperty;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by liuBo
 * 2020/1/10.
 */
public class CaptchaDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private WisSecurityConfigurationProperty wisSecurityConfigurationProperty;

    public CaptchaDaoAuthenticationProvider(WisSecurityConfigurationProperty properties) {
        this.wisSecurityConfigurationProperty=properties;
    }
    public boolean supports(Class<?> authentication) {
        //只接收UsernamePasswordAuthenticationToken
        return (authentication == UsernamePasswordAuthenticationToken.class);
    }
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken token)
            throws AuthenticationException {
        if(wisSecurityConfigurationProperty.getCaptcha()) {
            //有验证码时，进行验证码的校验
            Object obj = token.getDetails();
            if (!(obj instanceof CaptchaAuthenticationDetails)) {
                throw new InsufficientAuthenticationException(
                        "Captcha details not found.");
            }

            CaptchaAuthenticationDetails captchaDetails = (CaptchaAuthenticationDetails) obj;
            String expected = captchaDetails.getCaptcha();
            if (expected != null) {
                String actual = captchaDetails.getAnswer();
                if (!expected.equals(actual)) {
                    throw new BadCredentialsException("Captcha does not match.");
                }
            } else {
                throw new BadCredentialsException("Captcha does not match.");
            }
        }

        //没有超级密码时，用原始的方法校验
        super.additionalAuthenticationChecks(userDetails, token);

    }
}
