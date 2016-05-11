package uk.co.alumeni.prism.interceptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class Utf8InterceptorTest {

	@Test
	public void shouldSetCharacterEncodingToUTF8() throws Exception{
		Utf8Interceptor utf8Interceptor = new Utf8Interceptor();
		HttpServletRequest request = new MockHttpServletRequest();
		request.setCharacterEncoding("ASCII");

		assertTrue(utf8Interceptor.preHandle(request, null, null));

		assertEquals("UTF-8",request.getCharacterEncoding());
	}
}
