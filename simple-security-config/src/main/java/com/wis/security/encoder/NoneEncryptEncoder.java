package com.wis.security.encoder;

import com.wis.security.util.PasswordEncoderUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * Created by liuBo
 * 2020/1/29.
 */
public final class NoneEncryptEncoder implements PasswordEncoder{
	
	public NoneEncryptEncoder(){
	}
	public String encode(CharSequence rawPassword) {
		return rawPassword.toString();
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String pass1=encode(rawPassword);
		String pass2=""+encodedPassword;
		return PasswordEncoderUtils.equals(pass1, pass2);
	}

}
