package com.zuehlke.pgadmissions.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtils {

    private static final int DEFAULT_HASH_ITERATIONS = 0;
    
    private static final String SECURE_RANDOM_INSTANCE = "SHA1PRNG";
    
    private static final String DEFAULT_HASH_ALGORITHM = "MD5";
    
	private static final int PASSWORD_LENGTH = 8;
	
    public static final char[] DEFAULT_CHARACTER_SET = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9' };
	            
	private SecureRandom secureRandom = null;
	
	public EncryptionUtils() {
	    try {
	        secureRandom = SecureRandom.getInstance(SECURE_RANDOM_INSTANCE);
	    } catch (NoSuchAlgorithmException e) {
	        throw new IllegalArgumentException(e.getMessage(), e);
	    }
	}
	
    public String getSalt(final int saltLengthInBytes) {
        byte[] salt = new BigInteger(saltLengthInBytes, secureRandom).toByteArray();
        return Hex.encodeHexString(salt);
    }

    public String calculateHash(final String string, final String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(DEFAULT_HASH_ALGORITHM);
            
            digest.reset();
            
            if (StringUtils.isNotEmpty(salt)) {
                digest.update(Hex.decodeHex(salt.toCharArray()));
            }

            byte[] input = digest.digest(string.getBytes("UTF-8"));

            for (int i = 0; i < DEFAULT_HASH_ITERATIONS; i++) {
                digest.reset();
                input = digest.digest(input);
            }
            return Hex.encodeHexString(input);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (DecoderException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
	
	public String getMD5Hash(String string) {
	    return calculateHash(string, null);
	}
	
	public String generateUUID() {
		return UUID.randomUUID().toString();
	}

	public String generateUserPassword() {
	    return getRandomString(PASSWORD_LENGTH, DEFAULT_CHARACTER_SET);
	}
	
	public String getRandomString(int length, char[] characterSet) {
	    StringBuilder sb = new StringBuilder();
	    for (int loop = 0; loop < length; loop++) {
	        int index = secureRandom.nextInt(characterSet.length);
	        sb.append(characterSet[index]);
	    }
	    return sb.toString();
	}
}
