package com.zuehlke.pgadmissions.services;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.rest.representation.SocialPresenceRepresentation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
@SuppressWarnings("unused")
public class SocialPresenceServiceTest {
    
    @Autowired
    private InstitutionService institutionService;
    
    @Autowired
    private UserService userService;
    
    @Test
    public void shouldGetCompanyInformation() throws IOException {
        SocialPresenceRepresentation result = institutionService.getSocialProfiles("University College London");
    }
    
    @Test
    public void shouldGetUserInformation() throws IOException {
        SocialPresenceRepresentation result = userService.getSocialProfiles("Anthony Finkelstein");
    }
    
}
