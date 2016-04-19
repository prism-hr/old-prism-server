package uk.co.alumeni.prism.interceptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EncryptionHelperTest {

	private EncryptionHelper encryptionHelper;

	@Test
	public void shouldEncryptAndDecryptWithKeyFromSpec() {
		String random = RandomStringUtils.randomAlphanumeric(16);

		SecretKeySpec key = new SecretKeySpec(random.getBytes(), "AES");
		KeyContextHolder.setContext(key);
		String clearText="clearText";
		String encrypted = encryptionHelper.encrypt(clearText);
		assertFalse(encrypted.equals(clearText));

		assertEquals(clearText, encryptionHelper.decrypt(encrypted));
	}

	@Test
	public void shouldReturnNullForNullArgument() {
		String random = RandomStringUtils.randomAlphanumeric(16);

		SecretKeySpec key = new SecretKeySpec(random.getBytes(), "AES");
		KeyContextHolder.setContext(key);

		assertNull(encryptionHelper.encrypt((String)null));

	}

	@Test
	public void shouldEncryptAndDecryptIntergerWithKeyFromSpec() throws Exception{
		String random = RandomStringUtils.randomAlphanumeric(16);

		SecretKeySpec key = new SecretKeySpec(random.getBytes(), "AES");
		KeyContextHolder.setContext(key);
		Integer integer = 5;

		String encrypted = encryptionHelper.encrypt(integer);
		assertFalse("5".equals(encrypted));
		assertEquals(integer, encryptionHelper.decryptToInteger(encrypted));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shoulThrowIllegalArgumentExceptionIfCantDecryptAsInteger() throws Exception{
		String random = RandomStringUtils.randomAlphanumeric(16);

		SecretKeySpec key = new SecretKeySpec(random.getBytes(), "AES");
		KeyContextHolder.setContext(key);


		String encrypted = encryptionHelper.encrypt("hallo");

		encryptionHelper.decryptToInteger(encrypted);
	}

	@Before
	public void setUp() throws Exception{
		KeyContextHolder.clearContext();
		encryptionHelper = new EncryptionHelper();
	}

	@After
	public void tearDown(){
		KeyContextHolder.clearContext();
	}
}
