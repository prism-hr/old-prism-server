package com.zuehlke.pgadmissions.services;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
public class SocialPresenceServiceTest {
    
    @Autowired
    private InstitutionService institutionService;
    
    @Autowired
    private UserService userService;
    
    @Test
    public void shouldGetCompanyInformation() throws IOException {
        institutionService.getSocialProfiles("University College London");
    }
    
    @Test
    public void shouldGetUserInformation() throws IOException {
        userService.getSocialProfiles("Anthony", "Finkelstein");
    }
    
}
