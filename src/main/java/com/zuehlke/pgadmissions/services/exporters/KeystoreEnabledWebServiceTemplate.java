package com.zuehlke.pgadmissions.services.exporters;

import org.springframework.core.io.Resource;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;

public class KeystoreEnabledWebServiceTemplate extends WebServiceTemplate {

    private Resource keyStoreLocation;

    private String keyStorePassword;

    public KeystoreEnabledWebServiceTemplate() {
        super();
    }

    @Override
    public Object marshalSendAndReceive(String uri, final Object requestPayload,
            final WebServiceMessageCallback requestCallback) {
        try {
            System.getProperties().setProperty("javax.net.ssl.keyStore", keyStoreLocation.getFile().getAbsolutePath());
            System.getProperties().setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
        } catch (Exception e) {
            // do nothing
        }
        return super.marshalSendAndReceive(uri, requestPayload, requestCallback);
    }

    public Resource getKeyStoreLocation() {
        return keyStoreLocation;
    }

    public void setKeyStoreLocation(Resource keyStoreLocation) {
        this.keyStoreLocation = keyStoreLocation;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }
}
