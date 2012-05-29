package com.zuehlke.pgadmissions.interceptors;

import static org.junit.Assert.*;

import javax.crypto.SecretKey;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

public class EncryptionKeyInterceptorTest {

	@Test
	public void shouldGetKeyFromSessionAndPutInContetHolder() throws Exception{
		SecretKey keyMock = EasyMock.createMock(SecretKey.class);
		EncryptionKeyInterceptor interceptor = new EncryptionKeyInterceptor();
		MockHttpSession session = new MockHttpSession();
		session.putValue("key", keyMock);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
			
		assertTrue(interceptor.preHandle(request, null, null));
		assertSame(keyMock, KeyContextHolder.getContext());
	}

	@Test
	@Ignore
	public void shouldClearContext() throws Exception{
		SecretKey keyMock = EasyMock.createMock(SecretKey.class);
		EncryptionKeyInterceptor interceptor = new EncryptionKeyInterceptor();
		KeyContextHolder.setContext(keyMock);
			
		interceptor.postHandle(null,null, null, null);
		assertNull(KeyContextHolder.getContext());
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
