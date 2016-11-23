package uk.co.alumeni.prism.interceptors;

import org.easymock.EasyMock;
import org.junit.Test;

import javax.crypto.SecretKey;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class KeyContextHolderTest {

    @Test
    public void shouldHoldKeyAsContext() {
        SecretKey keyMock = EasyMock.createMock(SecretKey.class);
        KeyContextHolder.setContext(keyMock);
        assertSame(keyMock, KeyContextHolder.getContext());

        KeyContextHolder.clearContext();
        assertNull(KeyContextHolder.getContext());
    }


}
