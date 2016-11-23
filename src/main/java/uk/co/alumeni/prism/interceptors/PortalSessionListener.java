package uk.co.alumeni.prism.interceptors;

import org.apache.commons.lang.RandomStringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class PortalSessionListener implements HttpSessionListener {


    @Override
    public void sessionCreated(HttpSessionEvent se) {
        String random = getRandomString();
        SecretKeySpec key = new SecretKeySpec(random.getBytes(), "AES");
        se.getSession().setAttribute("key", key);
    }

    String getRandomString() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        return;
    }

}
