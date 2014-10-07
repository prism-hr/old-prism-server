package com.zuehlke.pgadmissions.integration;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.integration.helpers.SystemInitialisationHelper;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.services.SystemService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
@Ignore
public class IT1InitialiseSystem {
    
    @Autowired
    private SystemService systemService;

    @Autowired
    private MailSenderMock mailSenderMock;
    
    @Autowired
    private SystemInitialisationHelper systemInitialisationHelper;

    @Test
    public void run() throws Exception {
        for (int i = 0; i < 2; i++) {
            systemService.initializeSystem();

            systemInitialisationHelper.verifyScopeCreation();
            systemInitialisationHelper.verifyRoleCreation();
            systemInitialisationHelper.verifyActionCreation();
            systemInitialisationHelper.verifyStateCreation();

            systemInitialisationHelper.verifySystemCreation();
            systemInitialisationHelper.verifySystemUserCreation();

            systemInitialisationHelper.verifyNotificationTemplateCreation();
            systemInitialisationHelper.verifyStateDurationCreation();

            systemInitialisationHelper.verifyStateActionCreation();

            if (i == 0) {
                systemInitialisationHelper.verifySystemUserRegistration();
            }

            mailSenderMock.verify();
        }
    }

}
