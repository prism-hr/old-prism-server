package com.zuehlke.pgadmissions.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ActionRedaction;
import com.zuehlke.pgadmissions.domain.Configuration;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionEnhancement;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedaction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.integration.helpers.RegistrationHelper;
import com.zuehlke.pgadmissions.integration.helpers.WorkflowVerificationHelper;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.SystemService;

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
    
    @Autowired
    private WorkflowVerificationHelper workflowVerificationHelper;

    @Test
    @Transactional
    public void testSystemInitialisation() {
        for (int i = 0; i < 2; i++) {
            systemService.initialiseSystem();

            verifyScopeCreation();
            verifyRoleCreation();
            verifyActionCreation();
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
            
            verifyStateActionCreation();
            workflowVerificationHelper.verifyWorkflowConfiguration();
            
            verifyNotificationTemplateCreation(system);
            verifyStateDurationCreation();

            if (i == 0) {
                registrationHelper.registerAndActivateUser(systemUser, system,
                        PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
            }

            mailSenderMock.verify();
        }
    }

    private void verifyScopeCreation() {
        for (Scope scope : systemService.getScopes()) {
            assertEquals(scope.getId().getPrecedence(), scope.getPrecedence());
        }
    }

    private void verifyRoleCreation() {
        for (Role role : roleService.getRoles()) {
            assertEquals(role.getId().getScope(), role.getScope().getId());

            Set<Role> excludedRoles = role.getExcludedRoles();
            Set<PrismRole> prismExcludedRoles = PrismRole.getExcludedRoles(role.getId());

            assertEquals(prismExcludedRoles.size(), excludedRoles.size());

            for (Role excludedRole : role.getExcludedRoles()) {
                assertTrue(prismExcludedRoles.contains(excludedRole.getId()));
            }
        }
    }

    private void verifyActionCreation() {
        for (Action action : actionService.getActions()) {
            assertEquals(action.getId().getActionType(), action.getActionType());
            assertEquals(action.getId().getScope(), action.getScope().getId());

            Set<ActionRedaction> redactions = action.getRedactions();
            List<PrismActionRedaction> prismActionRedactions = action.getId().getRedactions();

            assertEquals(prismActionRedactions.size(), redactions.size());

            for (ActionRedaction redaction : redactions) {
                PrismActionRedaction prismActionRedaction = new PrismActionRedaction().withRole(redaction.getRole().getId()).withRedactionType(
                        redaction.getRedactionType());
                assertTrue(prismActionRedactions.contains(prismActionRedaction));
            }
        }
    }

    private void verifyStateCreation() {
        for (State state : stateService.getStates()) {
            assertEquals(PrismState.getParentState(state.getId()), state.getParentState().getId());
            assertEquals(state.getId().getSequenceOrder(), state.getSequenceOrder());
            assertEquals(state.getId().getScope(), state.getScope().getId());
        }
    }

    private void verifyConfigurationCreation() {
        for (Configuration configuration : systemService.getConfigurations()) {
            assertEquals(configuration.getParameter().getDefaultValue(), configuration.getValue());
        }
    }

    private void verifyNotificationTemplateCreation(System system) {
        for (NotificationTemplate template : notificationTemplateService.getTemplates()) {
            assertEquals(template.getId().getNotificationType(), template.getNotificationType());
            assertEquals(template.getId().getNotificationPurpose(), template.getNotificationPurpose());
            assertEquals(template.getId().getScope(), template.getScope().getId());
            assertEquals(PrismNotificationTemplate.getReminderTemplate(template.getId()), (template.getReminderTemplate()) == null ? null : template
                    .getReminderTemplate().getId());

            NotificationConfiguration configuration = notificationTemplateService.getConfiguration(system, template);
            assertEquals(configuration.getNotificationTemplate(), template);
            assertEquals(configuration.getNotificationTemplateVersion().getNotificationTemplate(), template);
            assertEquals(PrismNotificationTemplate.getReminderInterval(template.getId()), configuration.getReminderInterval());

            NotificationTemplateVersion version = configuration.getNotificationTemplateVersion();
            assertEquals(getFileContent(EMAIL_DEFAULT_SUBJECT_DIRECTORY + template.getId().getInitialTemplateSubject()), version.getSubject());
            assertEquals(getFileContent(EMAIL_DEFAULT_CONTENT_DIRECTORY + template.getId().getInitialTemplateContent()), version.getContent());
        }
    }

    private void verifyStateDurationCreation() {
        for (State state : stateService.getConfigurableStates()) {
            assertEquals(state.getId().getDuration(), systemService.getStateDuration(state).getDuration());
        }
    }

    private void verifyStateActionCreation() {
        Integer stateActionsExpected = 0;
        for (PrismState prismState : PrismState.values()) {
            stateActionsExpected = stateActionsExpected + PrismState.getStateActions(prismState).size();
        }

        List<StateAction> stateActions = stateService.getStateActions();
        assertTrue(stateActionsExpected == stateActions.size());

        for (StateAction stateAction : stateActions) {
            PrismStateAction prismStateAction = PrismState.getStateAction(stateAction.getState().getId(), stateAction.getAction().getId());
            assertNotNull(prismStateAction);
            assertEquals(prismStateAction.isRaisesUrgentFlag(), stateAction.isRaisesUrgentFlag());
            assertEquals(prismStateAction.isDefaultAction(), stateAction.isDefaultAction());
            assertEquals(prismStateAction.isPostComment(), stateAction.isPostComment());
            
            NotificationTemplate template = stateAction.getNotificationTemplate();
            PrismNotificationTemplate prismTemplate = prismStateAction.getNotificationTemplate();
            if (prismTemplate == null) {
                assertNull(template);
            } else {
                assertNotNull(template);
                assertEquals(prismTemplate, stateAction.getNotificationTemplate().getId());
            }

            verifyStateActionAssignmentCreation(stateAction, prismStateAction);
            verifyStateActionNotificationCreation(stateAction, prismStateAction);
            verifyStateTransitionCreation(stateAction, prismStateAction);
        }
    }

    private void verifyStateActionAssignmentCreation(StateAction stateAction, PrismStateAction prismStateAction) {
        Set<StateActionAssignment> stateActionAssignments = stateAction.getStateActionAssignments();
        assertTrue(prismStateAction.getAssignments().size() == stateActionAssignments.size());

        for (StateActionAssignment stateActionAssignment : stateActionAssignments) {
            PrismStateActionAssignment prismStateActionAssignment = new PrismStateActionAssignment().withRole(stateActionAssignment.getRole().getId());

            for (StateActionEnhancement stateActionEnhancement : stateActionAssignment.getEnhancements()) {
                Action delegatedAction = stateActionEnhancement.getDelegatedAction();
                prismStateActionAssignment.getEnhancements().add(
                        new PrismStateActionEnhancement().withEnhancement(stateActionEnhancement.getEnhancementType()).withDelegatedAction(
                                (PrismAction) (delegatedAction == null ? delegatedAction : delegatedAction.getId())));
            }

            assertTrue(prismStateAction.getAssignments().contains(prismStateActionAssignment));
        }
    }

    private void verifyStateActionNotificationCreation(StateAction stateAction, PrismStateAction prismStateAction) {
        Set<StateActionNotification> stateActionNotifications = stateAction.getStateActionNotifications();
        assertTrue(prismStateAction.getNotifications().size() == stateActionNotifications.size());

        for (StateActionNotification stateActionNotification : stateActionNotifications) {
            PrismStateActionNotification prismStateActionNotification = new PrismStateActionNotification().withRole(stateActionNotification.getRole().getId())
                    .withTemplate(stateActionNotification.getNotificationTemplate().getId());
            assertTrue(prismStateAction.getNotifications().contains(prismStateActionNotification));
        }
    }
    
    private void verifyStateTransitionCreation(StateAction stateAction, PrismStateAction prismStateAction) {
        Set<StateTransition> stateTransitions = stateAction.getStateTransitions();
        assertTrue(prismStateAction.getTransitions().size() == stateTransitions.size());

        for (StateTransition stateTransition : stateTransitions) {
            PrismStateTransition prismStateTransition = new PrismStateTransition().withTransitionState(stateTransition.getTransitionState().getId())
                    .withTransitionAction(stateTransition.getTransitionAction().getId())
                    .withTransitionEvaluation(stateTransition.getStateTransitionEvaluation());

            for (RoleTransition roleTransition : stateTransition.getRoleTransitions()) {
                prismStateTransition.getRoleTransitions().add(
                        new PrismRoleTransition().withRole(roleTransition.getRole().getId()).withTransitionType(roleTransition.getRoleTransitionType())
                                .withTransitionRole(roleTransition.getTransitionRole().getId())
                                .withRestrictToOwner(roleTransition.isRestrictToActionOwner()).withMinimumPermitted(roleTransition.getMinimumPermitted())
                                .withMaximumPermitted(roleTransition.getMaximumPermitted()));
            }
            
            for (Action propagatedAction : stateTransition.getPropagatedActions()) {
                prismStateTransition.getPropagatedActions().add(propagatedAction.getId());
            }
            
            assertTrue(prismStateAction.getTransitions().contains(prismStateTransition));
        }
    }

    private String getFileContent(String filePath) {
        try {
            return Joiner.on(java.lang.System.lineSeparator()).join(Resources.readLines(Resources.getResource(filePath), Charsets.UTF_8));
        } catch (IOException e) {
            throw new Error(e);
        }
    }

}
