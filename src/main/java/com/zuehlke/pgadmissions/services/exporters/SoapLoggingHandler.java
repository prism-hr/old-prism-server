package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoapLoggingHandler implements SOAPHandler<SOAPMessageContext> {

    private final Logger log = LoggerFactory.getLogger(SoapLoggingHandler.class);
    
    public SoapLoggingHandler() {
    }

    @Override
    public boolean handleMessage(final SOAPMessageContext context) {
        SOAPMessage msg = context.getMessage();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            msg.writeTo(out);
            log.info(String.valueOf(out.toByteArray()));
        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean handleFault(final SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(final MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

}
