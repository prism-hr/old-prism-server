package com.zuehlke.pgadmissions.integration;

import com.zuehlke.pgadmissions.integration.helpers.PropertyLoaderHelper;
import com.zuehlke.pgadmissions.integration.helpers.SystemDataImportHelper;
import com.zuehlke.pgadmissions.integration.helpers.SystemInitialisationHelper;
import com.zuehlke.pgadmissions.integration.helpers.WorkflowConfigurationHelper;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.services.SystemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
	private SystemDataImportHelper systemDataImportHelper;

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

			if (i == 0) {
				systemInitialisationHelper.verifySystemUserRegistration();
			}

			mailSenderMock.verify();
		}

		systemDataImportHelper.verifyImport();
		propertyLoaderHelper.verifyPropertyLoader();
	}

}
