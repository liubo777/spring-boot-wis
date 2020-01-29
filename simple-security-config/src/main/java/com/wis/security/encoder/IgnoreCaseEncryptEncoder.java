package com.wis.security.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * Created by liuBo
 * 2020/1/29.
 */
public final class IgnoreCaseEncryptEncoder implements PasswordEncoder{
	
	public IgnoreCaseEncryptEncoder() {
	}
	public String encode(CharSequence rawPassword) {
		return rawPassword.toString();
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String pass1=encode(rawPassword);
		String pass2=""+encodedPassword;
		return pass1.equalsIgnoreCase(pass2);
	}
}
