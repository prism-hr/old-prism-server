package uk.co.alumeni.prism.interceptors;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import javax.crypto.SecretKey;

import org.easymock.EasyMock;
import org.junit.Test;

public class KeyContextHolderTest {

	@Test
	public void shouldHoldKeyAsContext(){
		SecretKey keyMock = EasyMock.createMock(SecretKey.class);
		KeyContextHolder.setContext(keyMock);
		assertSame(keyMock, KeyContextHolder.getContext());

		KeyContextHolder.clearContext();
		assertNull(KeyContextHolder.getContext());
	}


}
