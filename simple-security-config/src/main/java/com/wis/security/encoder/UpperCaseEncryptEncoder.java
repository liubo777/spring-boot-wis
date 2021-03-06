package com.wis.security.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * RSA方式加密和验证
 * 如果有嵌套方式，则表示先用嵌套的处理器进行加密，最后用RSA方式加密，
 * 解密时要将原数据使用嵌套的处理器加密后，与RSA解密后的字符串进行比较
 * @author wh
 */
public class UpperCaseEncryptEncoder implements PasswordEncoder{

	private PasswordEncoder encoder=new NoneEncryptEncoder();
	
	public UpperCaseEncryptEncoder(PasswordEncoder encoder){
		this.encoder=encoder;
	}
	public UpperCaseEncryptEncoder() {
		this(new NoneEncryptEncoder());
	}
	public String encode(CharSequence rawPassword) {
		return encoder.encode(rawPassword).toUpperCase();
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		//先对原始数据进行加密，再对数据进行md5
		String pass1=rawPassword.toString().toUpperCase();
		String pass2=""+encodedPassword;
		return encoder.matches(pass1, pass2);
	}
}
