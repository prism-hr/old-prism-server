package uk.co.alumeni.prism.interceptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSessionEvent;

import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

public class PortalSessionListenerTest {

	@Test
	public void shouldReturnStringOfLength16(){
		PortalSessionListener portalSessionListener = new PortalSessionListener();
		String randomStringOne = portalSessionListener.getRandomString();
		assertEquals(16, randomStringOne.getBytes().length);

		String randomStringTwo = portalSessionListener.getRandomString();
		assertEquals(16, randomStringTwo.getBytes().length);

		assertFalse(randomStringOne.equals(randomStringTwo));

	}


	@Test
	public void shouldGenerateKeyAndPutInSession(){
		PortalSessionListener portalSessionListener = new PortalSessionListener();
		MockHttpSession session = new MockHttpSession();

		HttpSessionEvent sessionEvent = new HttpSessionEvent(session);
		portalSessionListener.sessionCreated(sessionEvent);
		assertNotNull(session.getAttribute("key"));
		assertTrue(session.getAttribute("key") instanceof SecretKeySpec);


	}
}
