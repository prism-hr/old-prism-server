package com.zuehlke.pgadmissions.interceptors;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class EncryptionHelper {

	private byte[] linebreak = {};

	public String encrypt(String plainText) {
		if(plainText == null){
			return null;
		}
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
			Base64 coder = new Base64(32, linebreak, true);
			SecretKey key = KeyContextHolder.getContext();
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] cipherText = cipher.doFinal(plainText.getBytes());
			return new String(coder.encode(cipherText));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 

	}

	public String decrypt(String codedText) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
			Base64 coder = new Base64(32, linebreak, true);
			SecretKey key = KeyContextHolder.getContext();
			byte[] encypted = coder.decode(codedText.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decrypted = cipher.doFinal(encypted);
			return new String(decrypted);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String encrypt(Integer integer) {
		return encrypt(integer.toString());
	}

	public Integer decryptToInteger(String encrypted) {
		return Integer.parseInt(decrypt(encrypted));
	}
	
}
