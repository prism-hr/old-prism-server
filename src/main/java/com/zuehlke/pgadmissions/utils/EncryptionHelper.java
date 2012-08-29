package com.zuehlke.pgadmissions.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class EncryptionHelper {

	private final static Random RANDOM = new SecureRandom();
	private final static int PASSWORD_LENGTH = 8;
	private final static char[] LETTER_STORE = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',// 
			'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E',// 
			'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',// 
			'2', '3', '4', '5', '6', '7', '8', '9' };

	public String getMD5Hash(String string) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(string.getBytes());
			byte byteData[] = md5.digest();
			// convert bytes to hex chars
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public String generateUUID() {
		return UUID.randomUUID().toString();
	}

	public String generateUserPassword() {
		RANDOM.setSeed(System.currentTimeMillis());

		StringBuilder pw = new StringBuilder();
		for (int i = 0; i < PASSWORD_LENGTH; i++) {
			int letterIndex = (int) (RANDOM.nextDouble() * LETTER_STORE.length);
			pw.append(LETTER_STORE[letterIndex]);
		}
		return pw.toString();
	}
}
