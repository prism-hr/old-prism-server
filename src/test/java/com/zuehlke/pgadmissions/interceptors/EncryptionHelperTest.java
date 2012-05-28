package com.zuehlke.pgadmissions.interceptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EncryptionHelperTest {

	@Test
	public void shouldEncryptAndDecryptWithKeyFromSpec() throws Exception{
		String random = RandomStringUtils.randomAlphanumeric(16);
		
		SecretKeySpec key = new SecretKeySpec(random.getBytes(), "AES");
		KeyContextHolder.setContext(key);
		String clearText="clearText";
		EncryptionHelper encryptionHelper = new EncryptionHelper();
		String encrypted = encryptionHelper.encrypt(clearText);
		assertFalse(encrypted.equals(clearText));
		
		assertEquals(clearText, encryptionHelper.decrypt(encrypted));
	}

	@Before
	public void setUp(){
		KeyContextHolder.clearContext();
	}

	@After
	public void tearDown(){
		KeyContextHolder.clearContext();
	}
}
