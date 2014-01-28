package com.zuehlke.pgadmissions.services;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class UclIrisProfileServiceTest {

    @Test
    public void shouldReturnValidProfile() {
        UclIrisProfileService service = new UclIrisProfileService() {
            @Override
            Document downloadIrisProfile(final String upi) throws IOException {
                return Jsoup.parse(FileUtils.readFileToString(new File("src/test/resources/Finkelstein_IRIS_profile.html"), "UTF-8"));
            }
        };
        Assert.assertEquals("Prof Anthony Finkelstein", service.geProfile("ACWFI64"));
    }
    
    @Test
    public void shouldNotReturnValidProfile() {
        UclIrisProfileService service = new UclIrisProfileService() {
            @Override
            Document downloadIrisProfile(final String upi) throws IOException {
                return Jsoup.parse(FileUtils.readFileToString(new File("src/test/resources/Finkelstein_IRIS_profile_error.html"), "UTF-8"));
            }
        };
        Assert.assertEquals(StringUtils.EMPTY, service.geProfile("XXXXXXX"));
    }
    
    @Test
    public void shouldReturnTrue() {
        UclIrisProfileService service = new UclIrisProfileService() {
            @Override
            Document downloadIrisProfile(final String upi) throws IOException {
                return Jsoup.parse(FileUtils.readFileToString(new File("src/test/resources/Finkelstein_IRIS_profile.html"), "UTF-8"));
            }
        };
        Assert.assertTrue(service.profileExists("ACWFI64"));
    }
    
    @Test
    public void shouldReturnFalse() {
        UclIrisProfileService service = new UclIrisProfileService() {
            @Override
            Document downloadIrisProfile(final String upi) throws IOException {
                return Jsoup.parse(FileUtils.readFileToString(new File("src/test/resources/Finkelstein_IRIS_profile_error.html"), "UTF-8"));
            }
        };
        Assert.assertFalse(service.profileExists("XXXXXXX"));
    }
}
