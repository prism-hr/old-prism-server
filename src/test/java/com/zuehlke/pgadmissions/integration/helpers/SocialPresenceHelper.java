package com.zuehlke.pgadmissions.integration.helpers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
@Transactional
public class SocialPresenceHelper {

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private UserService userService;

    public void verifySocialPresenceLookup() throws IOException {
        institutionService.getSocialProfiles("University College London");
    }

}
