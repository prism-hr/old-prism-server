package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class UclIrisProfileServiceTest {

    private UclIrisProfileService service;
    
    @Before
    public void setup() {
        this.service = new UclIrisProfileService();
    }
    
    @Test
    public void shouldReturnValidProfile() {
        Assert.assertEquals("Prof Anthony Finkelstein", service.geProfile("ACWFI64"));
    }
    
    @Test
    public void shouldNotReturnValidProfile() {
        Assert.assertEquals(StringUtils.EMPTY, service.geProfile("XXXXXXX"));
    }
    
    @Test
    public void shouldReturnTrue() {
        Assert.assertTrue(service.profileExists("ACWFI64"));
    }
    
    @Test
    public void shouldReturnFalse() {
        Assert.assertFalse(service.profileExists("XXXXXXX"));
    }
}
