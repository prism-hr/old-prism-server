package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import junit.framework.Assert;

import org.junit.Test;

public class EncryptionUtilsTest {

	@Test
	public void shouldGenerateMD5Hash() {
		String hashed = new EncryptionUtils().getMD5Hash("string");
		assertEquals("b45cffe084dd3d20d928bee85e7b0f21", hashed);
	}

	@Test
	public void shouldGenerate36CharacterString() {
		EncryptionUtils encryptionUtils = new EncryptionUtils();
		String randomString = encryptionUtils.generateUUID();
		assertNotNull(randomString);
		assertEquals(36, randomString.length());
		assertFalse(randomString.equals(encryptionUtils.generateUUID()));
	}

	@Test
	public void shouldGenerateNewUserPassword() {
		EncryptionUtils encryptionUtils = new EncryptionUtils();
		String genPassword = encryptionUtils.generateUserPassword();
		Assert.assertEquals(8, genPassword.length());
	}
}
