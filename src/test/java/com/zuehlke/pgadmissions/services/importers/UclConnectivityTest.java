package com.zuehlke.pgadmissions.services.importers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.referencedata.v1.jaxb.Countries;

public class UclConnectivityTest {

    @Test
    @Ignore
    public void eduroamConnectivityTest() throws JAXBException, MalformedURLException {
//        Needed for Java7
//        System.setProperty("jsse.enableSNIExtension", "false");
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("reference", "wiI2+sZm".toCharArray());
            }
        });
        Unmarshaller unmarshaller = JAXBContext.newInstance(Countries.class).createUnmarshaller();
        Countries countries = (Countries) unmarshaller.unmarshal(new URL("https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/countriesOfBirth.xml"));
        assertNotNull(countries);
        assertEquals("AD", countries.getCountry().get(0).getCode());
    }
}
