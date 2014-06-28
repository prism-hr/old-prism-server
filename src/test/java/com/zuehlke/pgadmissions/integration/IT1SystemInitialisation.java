package com.zuehlke.pgadmissions.integration;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismRole;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.integration.helpers.RegistrationHelper;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.services.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
public class IT1SystemInitialisation {

    private final String EMAIL_DEFAULT_SUBJECT_DIRECTORY = "email/subject/";

    private final String EMAIL_DEFAULT_CONTENT_DIRECTORY = "email/content/";

    @Value("${system.name}")
    private String systemName;

    @Value("${system.user.firstName}")
    private String systemUserFirstName;

    @Value("${system.user.lastName}")
    private String systemUserLastName;

    @Value("${system.user.email}")
    private String systemUserEmail;

    @Autowired
    private ActionService actionService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private NotificationService notificationTemplateService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private MailSenderMock mailSenderMock;

    @Autowired
    private RegistrationHelper registrationHelper;

    @Test
    @Transactional
    public void testSystemInitialisation() {
        for (int i = 0; i < 2; i++) {
            systemService.initialiseSystem();

            verifyScopeCreation();
            verifyActionCreation();
            verifyRoleCreation();
            verifyStateCreation();

            System system = systemService.getSystem();
            assertEquals(system.getName(), systemName);
            assertEquals(system.getState().getId(), PrismState.SYSTEM_APPROVED);

            User systemUser = system.getUser();
            assertEquals(systemUser.getFirstName(), systemUserFirstName);
            assertEquals(systemUser.getLastName(), systemUserLastName);
            assertEquals(systemUser.getEmail(), systemUserEmail);

            for (UserRole userRole : systemUser.getUserRoles()) {
                assertEquals(userRole.getRole().getId(), PrismRole.SYSTEM_ADMINISTRATOR);
            }

            verifyConfigurationCreation();
            verifyNotificationTemplateCreation(system);
            verifyStateDurationCreation();

            if (i == 0) {
                registrationHelper.assertActivationEmailRegisterAndActivateUser(systemUser, system, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
            }

            mailSenderMock.verify();
        }
    }

    private void verifyStateDurationCreation() {
        for (State state : stateService.getConfigurableStates()) {
            assertEquals(state.getId().getDuration(), systemService.getStateDuration(state).getDuration());
        }
    }

    private void verifyNotificationTemplateCreation(System system) {
        for (NotificationTemplate template : notificationTemplateService.getTemplates()) {
            assertEquals(template.getId().getNotificationType(), template.getNotificationType());
            assertEquals(template.getId().getNotificationPurpose(), template.getNotificationPurpose());
            assertEquals(template.getId().getScope(), template.getScope().getId());
            assertEquals(PrismNotificationTemplate.getReminderTemplate(template.getId()), (template.getReminderTemplate()) == null ? null : template.getReminderTemplate().getId());

            NotificationConfiguration configuration = notificationTemplateService.getConfiguration(system, template);
            assertEquals(configuration.getNotificationTemplate(), template);
            assertEquals(configuration.getNotificationTemplateVersion().getNotificationTemplate(), template);
            assertEquals(PrismNotificationTemplate.getReminderInterval(template.getId()), configuration.getReminderInterval());

            NotificationTemplateVersion version = configuration.getNotificationTemplateVersion();
            assertEquals(getFileContent(EMAIL_DEFAULT_SUBJECT_DIRECTORY + template.getId().getInitialTemplateSubject()), version.getSubject());
            assertEquals(getFileContent(EMAIL_DEFAULT_CONTENT_DIRECTORY + template.getId().getInitialTemplateContent()), version.getContent());
        }
    }

    private void verifyConfigurationCreation() {
        for (Configuration configuration : systemService.getConfigurations()) {
            assertEquals(configuration.getParameter().getDefaultValue(), configuration.getValue());
        }
    }

    private void verifyStateCreation() {
        for (State state : stateService.getStates()) {
            assertEquals(PrismState.getParentState(state.getId()), state.getParentState().getId());
            assertEquals(state.getId().getSequenceOrder(), state.getSequenceOrder());
            assertEquals(state.getId().getScope(), state.getScope().getId());
        }
    }

    private void verifyRoleCreation() {
        for (Role role : roleService.getRoles()) {
            assertEquals(role.getId().getScope(), role.getScope().getId());
        }
    }

    private void verifyActionCreation() {
        for (Action action : actionService.getActions()) {
            assertEquals(action.getId().getActionType(), action.getActionType());
            assertEquals(action.getId().getScope(), action.getScope().getId());
        }
    }

    private void verifyScopeCreation() {
        for (Scope scope : systemService.getScopes()) {
            assertEquals(scope.getId().getPrecedence(), scope.getPrecedence());
        }
    }

    private String getFileContent(String filePath) {
        try {
            return Joiner.on(java.lang.System.lineSeparator()).join(Resources.readLines(Resources.getResource(filePath), Charsets.UTF_8));
        } catch (IOException e) {
            throw new Error("Could not access default notification template", e);
        }
    }

}
