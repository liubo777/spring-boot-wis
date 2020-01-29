package com.wis.security.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * Created by liuBo
 * 2020/1/29.
 */
public class MD5EncryptEncoder implements PasswordEncoder{

	private PasswordEncoder encoder=new NoneEncryptEncoder();
	
	public MD5EncryptEncoder(PasswordEncoder encoder){
		this.encoder=encoder;
	}
	public MD5EncryptEncoder() {
		this(new NoneEncryptEncoder());
	}
	public String encode(CharSequence rawPassword) {
		return MD5Util.md5(encoder.encode(rawPassword));
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		//先对原始数据进行加密，再对数据进行md5
		String pass1=MD5Util.md5(rawPassword.toString());
		String pass2=""+encodedPassword;
		return encoder.matches(pass1, pass2);
	}
}
