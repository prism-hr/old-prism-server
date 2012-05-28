package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.interceptors.KeyContextHolder;

public class DomainObjectTest {
	@Test
	public void shouldBeEqualIfIdIsSame() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		objectOne.setId(1);
		DomainObject<Integer> objectTwo = new MyDomainObject();
		objectTwo.setId(1);
		assertTrue(objectOne.equals(objectTwo));
		assertTrue(objectTwo.equals(objectOne));
	}

	@Test
	public void shouldNotBeEqualIfDomainObjectIdIsNull() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		DomainObject<Integer> objectTwo = new MyDomainObject();
		assertFalse(objectOne.equals(objectTwo));
		assertFalse(objectTwo.equals(objectOne));

		objectOne.setId(1);
		assertFalse(objectOne.equals(objectTwo));
		assertFalse(objectTwo.equals(objectOne));

		objectOne.setId(null);
		objectTwo.setId(1);
		assertFalse(objectOne.equals(objectTwo));
		assertFalse(objectTwo.equals(objectOne));
	}

	@Test
	public void shouldNotBeEqualIfDomainObjectIdsAreDifferent() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		objectOne.setId(1);
		DomainObject<Integer> objectTwo = new MyDomainObject();
		objectTwo.setId(2);
		assertFalse(objectOne.equals(objectTwo));
		assertFalse(objectTwo.equals(objectOne));
	}

	@Test
	public void shouldNotBeSameIfOtherIsNull() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		objectOne.setId(1);
		assertFalse(objectOne.equals(null));

	}

	@Test
	public void shouldNotBeSameIfNotSameType() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		objectOne.setId(1);
		@SuppressWarnings("serial")
		DomainObject<Integer> objectTwo = new DomainObject<Integer>() {

			@Override
			public void setId(Integer id) {
				this.id = id;
			}

			@Override
			public Integer getId() {
				return id;
			}
		};
		objectOne.setId(1);
		objectTwo.setId(1);
		assertFalse(objectOne.equals(objectTwo));

	}

	@Test
	public void shouldHaveSameHashcodeIfDomainObjectNumberSame() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		objectOne.setId(1);
		DomainObject<Integer> objectTwo = new MyDomainObject();
		objectTwo.setId(1);
		assertEquals(objectOne.hashCode(), objectTwo.hashCode());

	}
	
	@Test
	public void shouldReturnEncryptedId() throws Exception{
		String random = RandomStringUtils.randomAlphanumeric(16);
		System.err.println(random);
		SecretKeySpec key = new SecretKeySpec(random.getBytes(), "AES");
		KeyContextHolder.setContext(key);
		Integer id = 5;
		EncryptionHelper encryptionHelper = new EncryptionHelper();
		String encryptedId = encryptionHelper.encrypt(id.toString());
		DomainObject<Integer> objectOne = new MyDomainObject();
		objectOne.setId(id);
		assertEquals(encryptedId, objectOne.getEncryptedId());
	}

	@Test
	public void shouldReturnNullIfIdIsNull() throws Exception{
		String random = RandomStringUtils.randomAlphanumeric(16);
		System.err.println(random);
		SecretKeySpec key = new SecretKeySpec(random.getBytes(), "AES");
		KeyContextHolder.setContext(key);

				
		DomainObject<Integer> objectOne = new MyDomainObject();

		assertNull(objectOne.getEncryptedId());
	}

	@SuppressWarnings("serial")
	public class MyDomainObject extends DomainObject<Integer> {

		@Override
		public void setId(Integer id) {
			this.id = id;

		}

		@Override
		public Integer getId() {
			return id;
		}
	}
}
