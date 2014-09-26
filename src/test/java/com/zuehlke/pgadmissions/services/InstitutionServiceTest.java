package com.zuehlke.pgadmissions.services;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.rest.representation.SocialPresenceRepresentation;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
public class InstitutionServiceTest {

    @Autowired
    private InstitutionService institutionService;
    
    @Test
    public void shouldGetLinkedinInformationFormCompany() throws IOException {
        SocialPresenceRepresentation result = institutionService.getSocialProfiles("University College London");
    }
    
}
