package uk.co.alumeni.prism.integration.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityType.getSystemOpportunityType;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration.NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration.STATE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionRedaction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateActionAssignment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateActionNotification;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTermination;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.display.DisplayPropertyConfiguration;
import uk.co.alumeni.prism.domain.display.DisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.ActionRedaction;
import uk.co.alumeni.prism.domain.workflow.NotificationConfiguration;
import uk.co.alumeni.prism.domain.workflow.NotificationDefinition;
import uk.co.alumeni.prism.domain.workflow.OpportunityType;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.domain.workflow.RoleTransition;
import uk.co.alumeni.prism.domain.workflow.Scope;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateAction;
import uk.co.alumeni.prism.domain.workflow.StateActionAssignment;
import uk.co.alumeni.prism.domain.workflow.StateActionNotification;
import uk.co.alumeni.prism.domain.workflow.StateDurationConfiguration;
import uk.co.alumeni.prism.domain.workflow.StateGroup;
import uk.co.alumeni.prism.domain.workflow.StateTermination;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.domain.workflow.StateTransitionEvaluation;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.CustomizationService;
import uk.co.alumeni.prism.services.NotificationService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.ScopeService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.services.SystemService;
import uk.co.alumeni.prism.utils.PrismFileUtils;

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
            assertEquals(action.getId().isSystemInvocationOnly(), BooleanUtils.toBoolean(action.getSystemInvocationOnly()));
            assertEquals(action.getId().getActionCategory(), action.getActionCategory());
            assertEquals(action.getId().isRatingAction(), action.getRatingAction());
            assertEquals(action.getId().isDeclinableAction(), action.getDeclinableAction());
            assertEquals(action.getId().isVisibleAction(), action.getVisibleAction());
            assertEquals(action.getId().getScope(), action.getScope().getId());

            if (action.getActionCategory() == CREATE_RESOURCE) {
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
        assertEquals(system.getName(), systemName);
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
            assertEquals(userRole.getRole().getId(), SYSTEM_ADMINISTRATOR);
        }
    }

    public void verifyDisplayPropertyCreation() {
        System system = systemService.getSystem();
        for (DisplayPropertyConfiguration configuration : localizationService.getAllLocalizedProperties()) {
            assertEquals(configuration.getResource(), system);

            DisplayPropertyDefinition displayProperty = configuration.getDefinition();
            PrismDisplayPropertyDefinition prismDisplayProperty = displayProperty.getId();

            OpportunityType opportunityType = configuration.getOpportunityType();
            assertEquals((opportunityType == null ? null : opportunityType.getId()),
                    (displayProperty.getScope().getOrdinal() > DEPARTMENT.ordinal() ? getSystemOpportunityType() : null));
            assertEquals(displayProperty.getCategory(), prismDisplayProperty.getCategory());
            assertEquals(configuration.getValue(), prismDisplayProperty.getDefaultValue());
            assertTrue(configuration.getSystemDefault());
        }
    }

    public void verifyNotificationCreation() {
        System system = systemService.getSystem();
        for (NotificationDefinition definition : notificationService.getDefinitions()) {
            PrismNotificationDefinition prismNotificationDefinition = definition.getId();

            assertEquals(prismNotificationDefinition.getNotificationType(), definition.getNotificationType());
            assertEquals(prismNotificationDefinition.getNotificationPurpose(), definition.getNotificationPurpose());
            assertEquals(prismNotificationDefinition.getScope(), definition.getScope().getId());

            NotificationConfiguration configuration = (NotificationConfiguration) customizationService.getConfiguration(NOTIFICATION, system, definition);

            OpportunityType opportunityType = configuration.getOpportunityType();
            assertEquals((opportunityType == null ? null : opportunityType.getId()),
                    (definition.getScope().getScopeCategory().hasOpportunityTypeConfigurations() ? getSystemOpportunityType() : null));
            assertEquals(configuration.getDefinition(), definition);
            assertTrue(configuration.getSystemDefault());

            Assert.assertEquals(PrismFileUtils.getContent(defaultEmailSubjectDirectory + prismNotificationDefinition.getInitialTemplateSubject()),
                    configuration.getSubject());
            assertEquals(PrismFileUtils.getContent(defaultEmailContentDirectory + prismNotificationDefinition.getInitialTemplateContent()),
                    configuration.getContent());
        }
    }

    public void verifyStateDurationCreation() {
        System system = systemService.getSystem();
        for (State state : stateService.getConfigurableStates()) {
            StateDurationConfiguration configuration = (StateDurationConfiguration) customizationService.getConfiguration(STATE_DURATION, system,
                    state.getStateDurationDefinition());

            OpportunityType opportunityType = configuration.getOpportunityType();
            assertEquals((opportunityType == null ? null : opportunityType.getId()),
                    (state.getScope().getOrdinal() > DEPARTMENT.ordinal() ? getSystemOpportunityType() : null));
            assertEquals(state.getId().getDefaultDuration().getDefaultDuration(), configuration.getDuration());
            assertTrue(configuration.getSystemDefault());
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
            assertEquals(prismStateAction.getRaisesUrgentFlag(), stateAction.getRaisesUrgentFlag());
            assertEquals(prismStateAction.getActionEnhancement(), stateAction.getActionEnhancement());

            NotificationDefinition template = stateAction.getNotificationDefinition();
            PrismNotificationDefinition prismTemplate = prismStateAction.getNotification();
            if (prismTemplate == null) {
                assertNull(template);
            }

            verifyStateActionAssignmentCreation(stateAction, prismStateAction);
            verifyStateActionNotificationCreation(stateAction, prismStateAction);
            verifyStateTransitionCreation(stateAction, prismStateAction);
        }

        verifyNotificationCreation();
        verifyStateDurationCreation();
    }

    private void verifyStateActionAssignmentCreation(StateAction stateAction, PrismStateAction prismStateAction) {
        Set<StateActionAssignment> stateActionAssignments = stateAction.getStateActionAssignments();
        assertTrue(prismStateAction.getAssignments().size() == stateActionAssignments.size());

        for (StateActionAssignment stateActionAssignment : stateActionAssignments) {
            PrismStateActionAssignment prismStateActionAssignment = new PrismStateActionAssignment().withRole(stateActionAssignment.getRole().getId())
                    .withExternalMode(stateActionAssignment.getExternalMode()).withActionEnhancement(stateActionAssignment.getActionEnhancement());
            assertTrue(prismStateAction.getAssignments().contains(prismStateActionAssignment));
        }
    }

    private void verifyStateActionNotificationCreation(StateAction stateAction, PrismStateAction prismStateAction) {
        Set<StateActionNotification> stateActionNotifications = stateAction.getStateActionNotifications();
        assertTrue(prismStateAction.getNotifications().size() == stateActionNotifications.size());

        for (StateActionNotification stateActionNotification : stateActionNotifications) {
            PrismStateActionNotification prismStateActionNotification = new PrismStateActionNotification().withRole(stateActionNotification.getRole().getId())
                    .withDefinition(stateActionNotification.getNotificationDefinition().getId());
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
                    .withStateTransitionEvaluation(evaluation == null ? null : evaluation.getId());

            for (RoleTransition roleTransition : stateTransition.getRoleTransitions()) {
                PrismRoleTransition prismRoleTransition = new PrismRoleTransition().withRole(roleTransition.getRole().getId())
                        .withTransitionType(roleTransition.getRoleTransitionType()).withTransitionRole(roleTransition.getTransitionRole().getId())
                        .withMinimumPermitted(roleTransition.getMinimumPermitted()).withMaximumPermitted(roleTransition.getMaximumPermitted());
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
