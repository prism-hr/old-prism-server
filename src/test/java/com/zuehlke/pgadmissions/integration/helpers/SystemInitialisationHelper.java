package com.zuehlke.pgadmissions.integration.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getSystemOpportunityType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedaction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTermination;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.ActionRedaction;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.workflow.StateActionNotification;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.domain.workflow.StateTermination;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.utils.FileUtils;

@Service
@Transactional
public class SystemInitialisationHelper {

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${system.name}")
    private String systemName;

    @Value("${system.helpdesk}")
    private String systemHelpdesk;

    @Value("${system.user.firstName}")
    private String systemUserFirstName;

    @Value("${system.user.lastName}")
    private String systemUserLastName;

    @Value("${system.user.email}")
    private String systemUserEmail;

    @Value("${system.default.email.subject.directory}")
    private String defaultEmailSubjectDirectory;

    @Value("${system.default.email.content.directory}")
    private String defaultEmailContentDirectory;

    @Autowired
    private ActionService actionService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private CustomizationService localizationService;

    public void verifyScopeCreation() {
        for (PrismScope scopeId : scopeService.getScopesDescending()) {
            Scope scope = scopeService.getById(scopeId);
            assertEquals(new Integer(scope.getId().ordinal()), scope.getOrdinal());
            assertEquals(scope.getId().getShortCode(), scope.getShortCode());
        }
    }

    public void verifyRoleCreation() {
        for (Role role : roleService.getRoles()) {
            assertEquals(role.getId().getScope(), role.getScope().getId());
        }
    }

    public void verifyActionCreation() {
        for (Action action : actionService.getActions()) {
            assertEquals(action.getId().getActionType(), action.getActionType());
            assertEquals(action.getId().getActionCategory(), action.getActionCategory());
            assertEquals(action.getId().isRatingAction(), action.getRatingAction());
            assertEquals(action.getId().isDeclinableAction(), action.getDeclinableAction());
            assertEquals(action.getId().isVisibleAction(), action.getVisibleAction());
            assertEquals(action.getId().getScope(), action.getScope().getId());

            if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
                assertEquals(action.getTransitionAction(), true);
            }

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

    public void verifyStateGroupCreation() {

        for (StateGroup stateGroup : stateService.getStateGroups()) {
            assertEquals(stateGroup.getId().ordinal(), stateGroup.getOrdinal());
            assertEquals(stateGroup.getId().getScope(), stateGroup.getScope().getId());
        }
    }

    public void verifyStateCreation() {
        for (State state : stateService.getStates()) {
            assertEquals(state.getId().getStateGroup(), state.getStateGroup().getId());
            assertEquals(state.getId().getStateGroup().getScope(), state.getScope().getId());
        }
    }

    public void verifyStateTransitionEvaluationCreation() {
        for (StateTransitionEvaluation stateTransitionEvaluation : stateService.getStateTransitionEvaluations()) {
            assertEquals(stateTransitionEvaluation.getId().isNextStateSelection(), stateTransitionEvaluation.isNextStateSelection());
            assertEquals(stateTransitionEvaluation.getId().getScope(), stateTransitionEvaluation.getScope().getId());
        }
    }

    public void verifySystemCreation() {
        System system = systemService.getSystem();
        assertEquals(system.getTitle(), systemName);
        assertEquals(system.getCode(), resourceService.generateResourceCode(system));
        assertEquals(system.getState().getId(), PrismState.SYSTEM_RUNNING);
        assertNotNull(system.getCipherSalt());
    }

    public void verifySystemUserCreation() {
        System system = systemService.getSystem();
        User systemUser = system.getUser();
        assertEquals(systemUser.getFirstName(), systemUserFirstName);
        assertEquals(systemUser.getLastName(), systemUserLastName);
        assertEquals(systemUser.getEmail(), systemUserEmail);

        for (UserRole userRole : systemUser.getUserRoles()) {
            assertEquals(userRole.getRole().getId(), PrismRole.SYSTEM_ADMINISTRATOR);
        }
    }

    public void verifyDisplayPropertyCreation() {
        System system = systemService.getSystem();
        for (DisplayPropertyConfiguration value : localizationService.getAllLocalizedProperties()) {
            assertEquals(value.getResource(), system);

            DisplayPropertyDefinition displayProperty = value.getDisplayPropertyDefinition();
            PrismDisplayPropertyDefinition prismDisplayProperty = displayProperty.getId();

            assertEquals(value.getOpportunityType(), displayProperty.getScope().getOrdinal() > INSTITUTION.ordinal() ? getSystemOpportunityType() : null);
            assertEquals(displayProperty.getCategory(), prismDisplayProperty.getCategory());
            assertEquals(value.getValue(), prismDisplayProperty.getDefaultValue());
            assertTrue(value.getSystemDefault());
        }
    }

    public void verifyNotificationTemplateCreation() {
        System system = systemService.getSystem();
        for (NotificationDefinition definition : notificationService.getDefinitions()) {
            PrismNotificationDefinition prismNotificationDefinition = definition.getId();

            assertEquals(prismNotificationDefinition.getNotificationType(), definition.getNotificationType());
            assertEquals(prismNotificationDefinition.getNotificationPurpose(), definition.getNotificationPurpose());
            assertEquals(prismNotificationDefinition.getScope(), definition.getScope().getId());
            assertEquals(prismNotificationDefinition.getReminderDefinition(), (definition.getReminderDefinition()) == null ? null : definition
                    .getReminderDefinition().getId());

            NotificationConfiguration configuration = (NotificationConfiguration) customizationService.getConfiguration(PrismConfiguration.NOTIFICATION,
                    system, definition);

            assertEquals(configuration.getOpportunityType(), definition.getScope().getOrdinal() > INSTITUTION.ordinal() ? getSystemOpportunityType() : null);
            assertEquals(configuration.getNotificationDefinition(), definition);
            assertEquals(prismNotificationDefinition.getDefaultReminderDuration(), configuration.getReminderInterval());
            assertTrue(configuration.getSystemDefault());

            assertEquals(FileUtils.getContent(defaultEmailSubjectDirectory + prismNotificationDefinition.getInitialTemplateSubject()),
                    configuration.getSubject());
            assertEquals(FileUtils.getContent(defaultEmailContentDirectory + prismNotificationDefinition.getInitialTemplateContent()),
                    configuration.getContent());
        }
    }

    public void verifyStateDurationCreation() {
        System system = systemService.getSystem();
        for (State state : stateService.getConfigurableStates()) {
            StateDurationConfiguration stateDurationConfiguration = (StateDurationConfiguration) customizationService.getConfiguration(
                    PrismConfiguration.STATE_DURATION, system, state.getStateDurationDefinition());

            assertEquals(stateDurationConfiguration.getOpportunityType(), state.getScope().getOrdinal() > INSTITUTION.ordinal() ? getSystemOpportunityType()
                    : null);
            assertEquals(state.getId().getDefaultDuration().getDefaultDuration(), stateDurationConfiguration.getDuration());
            assertTrue(stateDurationConfiguration.getSystemDefault());
        }
    }

    public void verifyStateActionCreation() {
        Integer stateActionsExpected = 0;
        for (PrismState prismState : PrismState.values()) {
            stateActionsExpected = stateActionsExpected + PrismState.getStateActions(prismState).size();
        }

        List<StateAction> stateActions = stateService.getStateActions();
        assertTrue(stateActionsExpected == stateActions.size());

        for (StateAction stateAction : stateActions) {
            PrismStateAction prismStateAction = PrismState.getStateAction(stateAction.getState().getId(), stateAction.getAction().getId());
            assertNotNull(prismStateAction);
            assertEquals(prismStateAction.isRaisesUrgentFlag(), stateAction.getRaisesUrgentFlag());
            assertEquals(prismStateAction.getActionEnhancement(), stateAction.getActionEnhancement());

            NotificationDefinition template = stateAction.getNotificationDefinition();
            PrismNotificationDefinition prismTemplate = prismStateAction.getNotification();
            if (prismTemplate == null) {
                assertNull(template);
            } else {
                assertNotNull(template);
                assertEquals(prismTemplate, stateAction.getNotificationDefinition().getId());
            }

            verifyStateActionAssignmentCreation(stateAction, prismStateAction);
            verifyStateActionNotificationCreation(stateAction, prismStateAction);
            verifyStateTransitionCreation(stateAction, prismStateAction);
        }

        verifyNotificationTemplateCreation();
        verifyStateDurationCreation();
    }

    private void verifyStateActionAssignmentCreation(StateAction stateAction, PrismStateAction prismStateAction) {
        Set<StateActionAssignment> stateActionAssignments = stateAction.getStateActionAssignments();
        assertTrue(prismStateAction.getAssignments().size() == stateActionAssignments.size());

        for (StateActionAssignment stateActionAssignment : stateActionAssignments) {
            PrismStateActionAssignment prismStateActionAssignment = new PrismStateActionAssignment().withRole(stateActionAssignment.getRole().getId())
                    .withPartnerMode(stateActionAssignment.getPartnerMode()).withActionEnhancement(stateActionAssignment.getActionEnhancement());
            assertTrue(prismStateAction.getAssignments().contains(prismStateActionAssignment));
        }
    }

    private void verifyStateActionNotificationCreation(StateAction stateAction, PrismStateAction prismStateAction) {
        Set<StateActionNotification> stateActionNotifications = stateAction.getStateActionNotifications();
        assertTrue(prismStateAction.getNotifications().size() == stateActionNotifications.size());

        for (StateActionNotification stateActionNotification : stateActionNotifications) {
            PrismStateActionNotification prismStateActionNotification = new PrismStateActionNotification().withRole(stateActionNotification.getRole().getId())
                    .withPartnerMode(stateActionNotification.getPartnerMode()).withDefinition(stateActionNotification.getNotificationDefinition().getId());
            assertTrue(prismStateAction.getNotifications().contains(prismStateActionNotification));
        }
    }

    private void verifyStateTransitionCreation(StateAction stateAction, PrismStateAction prismStateAction) {
        Set<StateTransition> stateTransitions = stateAction.getStateTransitions();
        assertTrue(prismStateAction.getTransitions().size() == stateTransitions.size());

        for (StateTransition stateTransition : stateTransitions) {
            StateTransitionEvaluation evaluation = stateTransition.getStateTransitionEvaluation();

            State transitionState = stateTransition.getTransitionState();
            PrismStateTransition prismStateTransition = new PrismStateTransition()
                    .withTransitionState(transitionState == null ? null : transitionState.getId())
                    .withTransitionAction(stateTransition.getTransitionAction().getId())
                    .withTransitionEvaluation(evaluation == null ? null : evaluation.getId());

            for (RoleTransition roleTransition : stateTransition.getRoleTransitions()) {
                WorkflowPropertyDefinition workflowPropertyDefinition = roleTransition.getWorkflowPropertyDefinition();
                PrismRoleTransition prismRoleTransition = new PrismRoleTransition().withRole(roleTransition.getRole().getId())
                        .withTransitionType(roleTransition.getRoleTransitionType()).withTransitionRole(roleTransition.getTransitionRole().getId())
                        .withMinimumPermitted(roleTransition.getMinimumPermitted()).withMaximumPermitted(roleTransition.getMaximumPermitted())
                        .withPropertyDefinition(workflowPropertyDefinition == null ? null : workflowPropertyDefinition.getId());
                prismRoleTransition.setRestrictToActionOwner(roleTransition.getRestrictToActionOwner());
                prismStateTransition.getRoleTransitions().add(prismRoleTransition);
            }

            for (Action propagatedAction : stateTransition.getPropagatedActions()) {
                prismStateTransition.getPropagatedActions().add(propagatedAction.getId());
            }

            for (StateTermination stateTermination : stateTransition.getStateTerminations()) {
                prismStateTransition.getStateTerminations().add(
                        new PrismStateTermination().withTerminationState(stateTermination.getTerminationState().getId()).withStateTerminationEvaluation(
                                stateTermination.getStateTerminationEvaluation()));
            }

            assertTrue(prismStateAction.getTransitions().contains(prismStateTransition));
        }
    }

}
