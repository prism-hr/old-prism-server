package com.zuehlke.pgadmissions.integration;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_COMPLETE_REGISTRATION_REQUEST;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.integration.helpers.PropertyLoaderHelper;
import com.zuehlke.pgadmissions.integration.helpers.SystemInitialisationHelper;
import com.zuehlke.pgadmissions.integration.helpers.WorkflowConfigurationHelper;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.services.SystemService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
public class LifeCycleTest {

    @Autowired
    private SystemService systemService;

    @Autowired
    private MailSenderMock mailSenderMock;

    @Autowired
    private SystemInitialisationHelper systemInitialisationHelper;

    @Autowired
    private PropertyLoaderHelper propertyLoaderHelper;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void run() throws Exception {
        for (int i = 0; i < 2; i++) {
            systemService.initializeWorkflow();
            systemService.initializeDisplayProperties();
            systemService.initializeSystemUser();

            systemInitialisationHelper.verifyScopeCreation();
            systemInitialisationHelper.verifyRoleCreation();
            systemInitialisationHelper.verifyActionCreation();
            systemInitialisationHelper.verifyStateCreation();

            systemInitialisationHelper.verifySystemCreation();
            systemInitialisationHelper.verifySystemUserCreation();

            systemInitialisationHelper.verifyNotificationTemplateCreation();
            systemInitialisationHelper.verifyStateDurationCreation();

            systemInitialisationHelper.verifyStateActionCreation();
            applicationContext.getBean(WorkflowConfigurationHelper.class).verifyWorkflowConfiguration();

            List<MailMessageDTO> messages = mailSenderMock.getSentMessages();
            assertEquals(1, messages.size());
            NotificationConfiguration configuration = messages.get(0).getConfiguration();
            assertEquals(SYSTEM_COMPLETE_REGISTRATION_REQUEST, configuration.getDefinition().getId());
            mailSenderMock.getSentMessages().clear();
        }

        propertyLoaderHelper.verifyPropertyLoader();
    }

}
