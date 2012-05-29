package com.zuehlke.pgadmissions.interceptors;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

public class EncryptionHelper {

	private static byte[] linebreak = {}; // Remove Base64 encoder default linebreak	
										
	private static Cipher cipher;
	private static Base64 coder;

	public EncryptionHelper() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException{
		cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
		coder = new Base64(32, linebreak, true);
	}
	
	public String encrypt(String plainText) throws Exception {
		SecretKey key = KeyContextHolder.getContext();
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = cipher.doFinal(plainText.getBytes());
		return new String(coder.encode(cipherText));
	}
	

	public   String decrypt(String codedText) throws Exception {
		SecretKey key = KeyContextHolder.getContext();
		byte[] encypted = coder.decode(codedText.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(encypted);
		return new String(decrypted);
	}
}
