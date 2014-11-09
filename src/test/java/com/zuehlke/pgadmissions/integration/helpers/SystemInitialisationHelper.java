package com.zuehlke.pgadmissions.integration.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramType.getSystemProgramType;
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

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.system.System;
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
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;
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

    @Value("${application.host}")
    private String systemHomepage;

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
    private EntityService entityService;

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

    @Autowired
    private UserHelper userHelper;

    public void verifyScopeCreation() {
        for (PrismScope scopeId : scopeService.getScopesDescending()) {
            Scope scope = scopeService.getById(scopeId);
            assertEquals(scope.getId().getPrecedence(), scope.getPrecedence());
            assertEquals(scope.getId().getShortCode(), scope.getShortCode());
        }
    }

    public void verifyRoleCreation() {
        for (Role role : roleService.getRoles()) {
            assertEquals(role.getId().getScope(), role.getScope().getId());
            assertEquals(role.getId().isScopeOwner(), role.getScopeCreator());

            Set<Role> excludedRoles = role.getExcludedRoles();
            Set<PrismRole> prismExcludedRoles = PrismRole.getExcludedRoles(role.getId());

            assertEquals(prismExcludedRoles.size(), excludedRoles.size());

            for (Role excludedRole : excludedRoles) {
                assertTrue(prismExcludedRoles.contains(excludedRole.getId()));
            }
        }
    }

    public void verifyActionCreation() {
        for (Action action : actionService.getActions()) {
            assertEquals(action.getId().getActionType(), action.getActionType());
            assertEquals(action.getId().getActionCategory(), action.getActionCategory());
            assertEquals(action.getId().isRatingAction(), action.getRatingAction());
            assertEquals(action.getId().isTransitionAction(), action.getTransitionAction());
            assertEquals(action.getId().isDeclinableAction(), action.getDeclinableAction());
            assertEquals(action.getId().isVisibleAction(), action.getVisibleAction());
            assertEquals(action.getId().isEmphasizedAction(), action.getEmphasizedAction());
            assertEquals(PrismAction.getFallBackAction(action.getId()), action.getFallbackAction().getId());
            assertEquals(action.getId().getScope(), action.getScope().getId());
            assertEquals(action.getId().getCreationScope(), action.getCreationScope() == null ? null : action.getCreationScope().getId());

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
            assertEquals(stateGroup.getId().getSequenceOrder(), stateGroup.getSequenceOrder());
            assertEquals(stateGroup.getId().isRepeatable(), stateGroup.isRepeatable());
            assertEquals(stateGroup.getId().getScope(), stateGroup.getScope().getId());
        }
    }

    public void verifyStateCreation() {
        for (State state : stateService.getStates()) {
            assertEquals(state.getId().getStateGroup(), state.getStateGroup().getId());
            assertEquals(state.getId().getScope(), state.getScope().getId());
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
        assertEquals(system.getLocale(), PrismLocale.getSystemLocale());
        assertEquals(system.getHomepage(), systemHomepage);
        assertEquals(system.getHelpdesk(), systemHelpdesk);
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
            assertEquals(value.getLocale(), system.getLocale());

            DisplayPropertyDefinition displayProperty = value.getDisplayPropertyDefinition();
            PrismDisplayProperty prismDisplayProperty = (PrismDisplayProperty) displayProperty.getId();

            assertEquals(value.getProgramType(), displayProperty.getScope().getPrecedence() > INSTITUTION.getPrecedence() ? getSystemProgramType() : null);
            assertEquals(displayProperty.getDisplayCategory(), prismDisplayProperty.getDisplayCategory());
            assertEquals(value.getValue(), prismDisplayProperty.getDefaultValue());
            assertTrue(value.getSystemDefault());
        }
    }

    public void verifyNotificationTemplateCreation() {
        System system = systemService.getSystem();
        for (NotificationDefinition template : notificationService.getDefinitions()) {
            PrismNotificationDefinition prismNotificationTemplate = template.getId();

            assertEquals(prismNotificationTemplate.getNotificationType(), template.getNotificationType());
            assertEquals(prismNotificationTemplate.getNotificationPurpose(), template.getNotificationPurpose());
            assertEquals(prismNotificationTemplate.getScope(), template.getScope().getId());
            assertEquals(prismNotificationTemplate.getReminderTemplate(), (template.getReminderDefinition()) == null ? null : template.getReminderDefinition()
                    .getId());

            PrismProgramType programType = template.getScope().getPrecedence() > INSTITUTION.getPrecedence() ? getSystemProgramType() : null;

            NotificationConfiguration configuration = notificationService.getConfigurationStrict(system, system.getLocale(), programType, template);
            assertEquals(configuration.getLocale(), getSystemLocale());
            assertEquals(configuration.getProgramType(), programType);
            assertEquals(configuration.getNotificationDefinition(), template);
            assertEquals(prismNotificationTemplate.getDefaultReminderDuration(), configuration.getReminderInterval());
            assertTrue(configuration.getSystemDefault());

            assertEquals(FileUtils.getContent(defaultEmailSubjectDirectory + prismNotificationTemplate.getInitialTemplateSubject()), configuration.getSubject());
            assertEquals(FileUtils.getContent(defaultEmailContentDirectory + prismNotificationTemplate.getInitialTemplateContent()), configuration.getContent());
        }
    }

    public void verifyStateDurationCreation() {
        System system = systemService.getSystem();
        for (State state : stateService.getConfigurableStates()) {
            StateDurationConfiguration stateDuration = stateService.getStateDurationConfiguration(system, system.getUser(), state);

            assertEquals(stateDuration.getLocale(), getSystemLocale());
            assertEquals(stateDuration.getProgramType(), state.getScope().getPrecedence() > INSTITUTION.getPrecedence() ? getSystemProgramType() : null);
            assertEquals(state.getId().getDuration(), stateDuration.getDuration());
            assertTrue(stateDuration.getSystemDefault());
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
            assertEquals(prismStateAction.isDefaultAction(), stateAction.isDefaultAction());
            assertEquals(prismStateAction.getActionEnhancement(), stateAction.getActionEnhancement());

            NotificationDefinition template = stateAction.getNotificationDefinition();
            PrismNotificationDefinition prismTemplate = prismStateAction.getNotificationTemplate();
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

    public void verifySystemUserRegistration() throws Exception {
        System system = systemService.getSystem();
        userHelper.registerAndActivateUser(PrismAction.SYSTEM_STARTUP, system.getId(), system.getUser(),
                PrismNotificationDefinition.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
    }

    private void verifyStateActionAssignmentCreation(StateAction stateAction, PrismStateAction prismStateAction) {
        Set<StateActionAssignment> stateActionAssignments = stateAction.getStateActionAssignments();
        assertTrue(prismStateAction.getAssignments().size() == stateActionAssignments.size());

        for (StateActionAssignment stateActionAssignment : stateActionAssignments) {
            Action delegatedAction = stateActionAssignment.getDelegatedAction();
            PrismStateActionAssignment prismStateActionAssignment = new PrismStateActionAssignment().withRole(stateActionAssignment.getRole().getId())
                    .withActionEnhancement(stateActionAssignment.getActionEnhancement())
                    .withDelegatedAction(delegatedAction == null ? null : delegatedAction.getId());
            assertTrue(prismStateAction.getAssignments().contains(prismStateActionAssignment));
        }
    }

    private void verifyStateActionNotificationCreation(StateAction stateAction, PrismStateAction prismStateAction) {
        Set<StateActionNotification> stateActionNotifications = stateAction.getStateActionNotifications();
        assertTrue(prismStateAction.getNotifications().size() == stateActionNotifications.size());

        for (StateActionNotification stateActionNotification : stateActionNotifications) {
            PrismStateActionNotification prismStateActionNotification = new PrismStateActionNotification().withRole(stateActionNotification.getRole().getId())
                    .withTemplate(stateActionNotification.getNotificationDefinition().getId());
            assertTrue(prismStateAction.getNotifications().contains(prismStateActionNotification));
        }
    }

    private void verifyStateTransitionCreation(StateAction stateAction, PrismStateAction prismStateAction) {
        Set<StateTransition> stateTransitions = stateAction.getStateTransitions();
        assertTrue(prismStateAction.getTransitions().size() == stateTransitions.size());

        for (StateTransition stateTransition : stateTransitions) {
            StateTransitionEvaluation evaluation = stateTransition.getStateTransitionEvaluation();

            PrismStateTransition prismStateTransition = new PrismStateTransition().withTransitionState(stateTransition.getTransitionState().getId())
                    .withTransitionAction(stateTransition.getTransitionAction().getId())
                    .withTransitionEvaluation(evaluation == null ? null : evaluation.getId());

            for (RoleTransition roleTransition : stateTransition.getRoleTransitions()) {
                prismStateTransition.getRoleTransitions().add(
                        new PrismRoleTransition().withRole(roleTransition.getRole().getId()).withTransitionType(roleTransition.getRoleTransitionType())
                                .withTransitionRole(roleTransition.getTransitionRole().getId()).withRestrictToOwner(roleTransition.getRestrictToActionOwner())
                                .withMinimumPermitted(roleTransition.getMinimumPermitted()).withMaximumPermitted(roleTransition.getMaximumPermitted()));
            }

            for (Action propagatedAction : stateTransition.getPropagatedActions()) {
                prismStateTransition.getPropagatedActions().add(propagatedAction.getId());
            }

            assertTrue(prismStateAction.getTransitions().contains(prismStateTransition));
        }
    }

}
