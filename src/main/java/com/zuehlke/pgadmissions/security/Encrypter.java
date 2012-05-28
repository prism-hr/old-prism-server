package com.zuehlke.pgadmissions.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class Encrypter {

	/**
	 * A simple text cipher to encrypt/decrypt a string.
	 */

	private static byte[] linebreak = {}; // Remove Base64 encoder default
											// linebreak
	private static String secret = "tvnw63ufg9gh5392"; // secret key length must
	private static SecretKey key;
														// be 16
	private static Cipher cipher;
	private static Base64 coder;

	static {
		try {
			key = new SecretKeySpec(secret.getBytes(), "AES");
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
			coder = new Base64(32, linebreak, true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static synchronized String encrypt(String plainText) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = cipher.doFinal(plainText.getBytes());
		return new String(coder.encode(cipherText));
	}

	public static synchronized String decrypt(String codedText) throws Exception {
		byte[] encypted = coder.decode(codedText.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(encypted);
		return new String(decrypted);
	}

	public static void main(String[] arg){
		try {
			String encrypted = encrypt("Hello karen");
			System.err.println(encrypted);
			System.err.println(decrypt("iOqJTNy4_0r9p102JztuqQ"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
