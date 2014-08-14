package com.zuehlke.pgadmissions.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.integration.helpers.SystemInitialisationHelper;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.services.SystemService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
public class IT1InitialiseSystem implements IPrismIntegrationTest {
    
    @Autowired
    private SystemService systemService;

    @Autowired
    private MailSenderMock mailSenderMock;
    
    @Autowired
    private SystemInitialisationHelper systemInitialisationHelper;
    
    @Autowired
    private IntegrationTestRunner integrationTestRunner;

    @Test
    @Override
    public void run() throws WorkflowConfigurationException, WorkflowEngineException {
        for (int i = 0; i < 2; i++) {
            systemService.initialiseSystem();

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
        integrationTestRunner.recordTestSuccess(this);
    }

}
