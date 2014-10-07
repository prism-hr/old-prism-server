package com.zuehlke.pgadmissions.services;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.*;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class SystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemService.class);

    @Value("${system.name}")
    private String systemName;

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

    @Value("${startup.workflow.initialize.notifications}")
    private Boolean initializeNotifications;

    @Autowired
    private EntityService entityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private StateService stateService;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public System getSystem() {
        return entityService.getByProperty(System.class, "title", systemName);
    }

    @Transactional(timeout = 600)
    public void initialiseSystem() throws WorkflowConfigurationException, DeduplicationException {
        LOGGER.info("Initialising scope definitions");
        verifyBackwardCompatibility(Scope.class);
        initialiseScopes();

        LOGGER.info("Initialising role definitions");
        verifyBackwardCompatibility(Role.class);
        initialiseRoles();

        LOGGER.info("Initialising action definitions");
        verifyBackwardCompatibility(Action.class);
        initialiseActions();

        LOGGER.info("Initialising state group definitions");
        verifyBackwardCompatibility(StateGroup.class);
        initialiseStateGroups();

        LOGGER.info("Initialising state definitions");
        verifyBackwardCompatibility(State.class);
        initialiseStates();

        LOGGER.info("Initialising state transition evaluation definitions");
        verifyBackwardCompatibility(StateTransitionEvaluation.class);
        initialiseStateTransitionEvaluations();

        LOGGER.info("Initialising system");
        System system = initialiseSystemResource();

        LOGGER.info("Initialising notification definitions");
        initialiseNotificationTemplates(system);

        LOGGER.info("Initialising state duration definitions");
        initialiseStateDurations(system);

        LOGGER.info("Initialising workflow definitions");
        initialiseStateActions();

        LOGGER.info("Initialising system user");
        initialiseSystemUser(system);

        entityService.flush();
        entityService.clear();
    }

    @Transactional
    public void initialiseSearchIndex() throws InterruptedException {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        fullTextSession.createIndexer().startAndWait();
    }

    @Transactional
    public void setLastDataImportDate(LocalDate baseline) {
        getSystem().setLastDataImportDate(baseline);
    }

    private void initialiseScopes() throws DeduplicationException {
        for (PrismScope prismScope : PrismScope.values()) {
            Scope transientScope = new Scope().withId(prismScope).withPrecedence(prismScope.getPrecedence()).withShortCode(prismScope.getShortCode());
            entityService.createOrUpdate(transientScope);
        }
    }

    private void initialiseRoles() throws DeduplicationException {
        roleService.deleteExcludedRoles();
        Set<Role> rolesWithExclusions = Sets.newHashSet();

        for (PrismRole prismRole : PrismRole.values()) {
            Scope scope = entityService.getById(Scope.class, prismRole.getScope());
            Role transientRole = new Role().withId(prismRole).withScopeCreator(prismRole.isScopeOwner()).withScope(scope);
            Role role = entityService.createOrUpdate(transientRole);
            role.getExcludedRoles().clear();

            if (!PrismRole.getExcludedRoles(prismRole).isEmpty()) {
                rolesWithExclusions.add(role);
            }
        }

        for (Role roleWithExclusions : rolesWithExclusions) {
            for (PrismRole excludedPrismRole : PrismRole.getExcludedRoles(roleWithExclusions.getId())) {
                Role excludedRole = roleService.getById(excludedPrismRole);
                roleWithExclusions.getExcludedRoles().add(excludedRole);
            }
        }
    }

    private void initialiseActions() throws DeduplicationException {
        entityService.deleteAll(ActionRedaction.class);

        for (PrismAction prismAction : PrismAction.values()) {
            Scope scope = entityService.getById(Scope.class, prismAction.getScope());
            Scope creationScope = entityService.getById(Scope.class, prismAction.getCreationScope());
            Action transientAction = new Action().withId(prismAction).withActionType(prismAction.getActionType())
                    .withActionCategory(prismAction.getActionCategory()).withRatingAction(prismAction.isRatingAction())
                    .withTransitionAction(prismAction.isTransitionAction()).withScope(scope).withCreationScope(creationScope);
            Action action = entityService.createOrUpdate(transientAction);
            action.getRedactions().clear();

            for (PrismActionRedaction prismActionRedaction : prismAction.getRedactions()) {
                Role role = roleService.getById(prismActionRedaction.getRole());
                ActionRedaction transientActionRedaction = new ActionRedaction().withAction(action).withRole(role)
                        .withRedactionType(prismActionRedaction.getRedactionType());
                ActionRedaction actionRedaction = entityService.createOrUpdate(transientActionRedaction);
                action.getRedactions().add(actionRedaction);
            }
        }

        for (PrismAction prismAction : PrismAction.values()) {
            Action action = actionService.getById(prismAction);
            Action fallbackAction = actionService.getById(PrismAction.getFallBackAction(prismAction));
            action.setFallbackAction(fallbackAction);
        }
    }

    private void initialiseStateGroups() throws DeduplicationException {
        for (PrismStateGroup prismStateGroup : PrismStateGroup.values()) {
            Scope scope = entityService.getById(Scope.class, prismStateGroup.getScope());
            StateGroup transientStateGroup = new StateGroup().withId(prismStateGroup).withSequenceOrder(prismStateGroup.getSequenceOrder())
                    .withRepeatable(prismStateGroup.isRepeatable()).withScope(scope);
            entityService.createOrUpdate(transientStateGroup);
        }
    }

    private void initialiseStates() throws DeduplicationException {
        for (PrismState prismState : PrismState.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismState.getScope());
            StateGroup stateGroup = entityService.getByProperty(StateGroup.class, "id", prismState.getStateGroup());
            State transientState = new State().withId(prismState).withStateGroup(stateGroup).withScope(scope);
            entityService.createOrUpdate(transientState);
        }
    }

    private void initialiseStateTransitionEvaluations() throws DeduplicationException {
        for (PrismStateTransitionEvaluation prismTransitionEvaluation : PrismStateTransitionEvaluation.values()) {
            Scope scope = entityService.getById(Scope.class, prismTransitionEvaluation.getScope());
            StateTransitionEvaluation transientStateTransitionEvaluation = new StateTransitionEvaluation().withId(prismTransitionEvaluation)
                    .withNextStateSelection(prismTransitionEvaluation.isNextStateSelection()).withScope(scope);
            entityService.createOrUpdate(transientStateTransitionEvaluation);
        }
    }

    private System initialiseSystemResource() throws DeduplicationException {
        User systemUser = userService.getOrCreateUser(systemUserFirstName, systemUserLastName, systemUserEmail);
        State systemRunning = stateService.getById(PrismState.SYSTEM_RUNNING);
        DateTime startupTimestamp = new DateTime();
        System transientSystem = new System().withTitle(systemName).withLocale(PrismLocale.getSystemLocale()).withUser(systemUser).withState(systemRunning)
                .withCreatedTimestamp(startupTimestamp).withUpdatedTimestamp(startupTimestamp);
        System system = entityService.createOrUpdate(transientSystem);
        system.setCode(resourceService.generateResourceCode(system));
        return system;
    }

    private void initialiseNotificationTemplates(System system) throws DeduplicationException {
        List<NotificationTemplate> processedTemplates = Lists.newArrayList();

        for (PrismNotificationTemplate prismTemplate : PrismNotificationTemplate.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismTemplate.getScope());

            NotificationTemplate transientTemplate = new NotificationTemplate().withId(prismTemplate).withNotificationType(prismTemplate.getNotificationType())
                    .withNotificationPurpose(prismTemplate.getNotificationPurpose()).withScope(scope);
            NotificationTemplate persistentTemplate = entityService.getDuplicateEntity(transientTemplate);

            if (persistentTemplate == null) {
                entityService.save(transientTemplate);
                processedTemplates.add(transientTemplate);
            } else {
                processedTemplates.add(persistentTemplate);
            }
        }

        for (NotificationTemplate processedTemplate : processedTemplates) {
            initialiseNotificationConfiguration(system, processedTemplate);
        }
    }

    private void initialiseNotificationConfiguration(System system, NotificationTemplate template) throws DeduplicationException {
        NotificationTemplate reminderTemplate = notificationService.getById(template.getId().getReminderTemplate());
        template.setReminderTemplate(reminderTemplate);
        PrismNotificationTemplate templateId = template.getId();

        NotificationConfiguration transientConfiguration = new NotificationConfiguration().withResource(system).withNotificationTemplate(template)
                .withSubject(getFileContent(defaultEmailSubjectDirectory + templateId.getInitialTemplateSubject()))
                .withContent(getFileContent(defaultEmailContentDirectory + templateId.getInitialTemplateContent()))
                .withReminderInterval(template.getId().getReminderInterval());
        NotificationConfiguration persistentConfiguration = entityService.getDuplicateEntity(transientConfiguration);

        if (persistentConfiguration == null) {
            entityService.save(transientConfiguration);
        } else if (BooleanUtils.isTrue(initializeNotifications)) {
            persistentConfiguration.setSubject(transientConfiguration.getSubject());
            persistentConfiguration.setContent(transientConfiguration.getContent());
        }
    }

    private void initialiseStateDurations(System system) throws DeduplicationException {
        for (PrismState prismState : PrismState.values()) {
            if (prismState.getDuration() != null) {
                State state = stateService.getById(prismState);
                StateDuration transientStateDuration = new StateDuration().withSystem(system).withState(state).withDuration(prismState.getDuration());
                entityService.createOrUpdate(transientStateDuration);
            }
        }
    }

    private void initialiseStateActions() throws DeduplicationException, WorkflowConfigurationException {
        stateService.deleteStateActions();

        for (State state : stateService.getStates()) {
            for (PrismStateAction prismStateAction : PrismState.getStateActions(state.getId())) {
                Action action = actionService.getById(prismStateAction.getAction());
                NotificationTemplate template = notificationService.getById(prismStateAction.getNotificationTemplate());
                StateAction stateAction = new StateAction().withState(state).withAction(action).withRaisesUrgentFlag(prismStateAction.isRaisesUrgentFlag())
                        .withDefaultAction(prismStateAction.isDefaultAction()).withActionEnhancement(prismStateAction.getActionEnhancement())
                        .withNotificationTemplate(template);
                entityService.save(stateAction);
                state.getStateActions().add(stateAction);

                initialiseStateActionAssignments(prismStateAction, stateAction);
                initialiseStateActionNotifications(prismStateAction, stateAction);
                initialiseStateTransitions(prismStateAction, stateAction);
            }
        }

        stateService.deleteObsoleteStateDurations();
        notificationService.deleteObsoleteNotificationConfigurations();
        roleService.deleteInactiveRoles();
    }

    private void initialiseStateActionAssignments(PrismStateAction prismStateAction, StateAction stateAction) {
        for (PrismStateActionAssignment prismAssignment : prismStateAction.getAssignments()) {
            Role role = roleService.getById(prismAssignment.getRole());
            Action delegateAction = actionService.getById(prismAssignment.getDelegatedAction());
            StateActionAssignment assignment = new StateActionAssignment().withStateAction(stateAction).withRole(role)
                    .withActionEnhancement(prismAssignment.getActionEnhancement()).withDelegatedAction(delegateAction);
            entityService.save(assignment);
            stateAction.getStateActionAssignments().add(assignment);
        }
    }

    private void initialiseStateActionNotifications(PrismStateAction prismStateAction, StateAction stateAction) {
        for (PrismStateActionNotification prismNotification : prismStateAction.getNotifications()) {
            Role role = roleService.getById(prismNotification.getRole());
            NotificationTemplate template = notificationService.getById(prismNotification.getTemplate());
            StateActionNotification notification = new StateActionNotification().withStateAction(stateAction).withRole(role).withNotificationTemplate(template);
            entityService.save(notification);
            stateAction.getStateActionNotifications().add(notification);
        }
    }

    private void initialiseStateTransitions(PrismStateAction prismStateAction, StateAction stateAction) {
        for (PrismStateTransition prismStateTransition : prismStateAction.getTransitions()) {
            State transitionState = stateService.getById(prismStateTransition.getTransitionState());
            Action transitionAction = actionService.getById(prismStateTransition.getTransitionAction());
            StateTransitionEvaluation transitionEvaluation = stateService.getStateTransitionEvaluationById(prismStateTransition.getTransitionEvaluation());
            StateTransition stateTransition = new StateTransition().withStateAction(stateAction).withTransitionState(transitionState)
                    .withTransitionAction(transitionAction).withStateTransitionEvaluation(transitionEvaluation);
            entityService.save(stateTransition);
            stateAction.getStateTransitions().add(stateTransition);
            initialiseRoleTransitions(prismStateTransition, stateTransition);

            Set<Action> propagatedActions = stateTransition.getPropagatedActions();
            for (PrismAction prismAction : prismStateTransition.getPropagatedActions()) {
                Action action = actionService.getById(prismAction);
                propagatedActions.add(action);
            }
        }
    }

    private void initialiseRoleTransitions(PrismStateTransition prismStateTransition, StateTransition stateTransition) {
        for (PrismRoleTransition prismRoleTransition : prismStateTransition.getRoleTransitions()) {
            Role role = roleService.getById(prismRoleTransition.getRole());
            Role transitionRole = roleService.getById(prismRoleTransition.getTransitionRole());
            RoleTransition roleTransition = new RoleTransition().withStateTransition(stateTransition).withRole(role)
                    .withRoleTransitionType(prismRoleTransition.getTransitionType()).withTransitionRole(transitionRole)
                    .withRestrictToActionOwner(prismRoleTransition.isRestrictToActionOwner()).withMinimumPermitted(prismRoleTransition.getMinimumPermitted())
                    .withMaximumPermitted(prismRoleTransition.getMaximumPermitted());
            entityService.save(roleTransition);
            stateTransition.getRoleTransitions().add(roleTransition);
        }
    }

    private void initialiseSystemUser(System system) throws DeduplicationException {
        User user = system.getUser();
        if (user.getUserAccount() == null) {
            Action action = actionService.getById(PrismAction.SYSTEM_STARTUP);
            Comment comment = new Comment().withUser(user).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                    .addAssignedUser(user, roleService.getCreatorRole(system), PrismRoleTransitionType.CREATE);
            ActionOutcomeDTO outcome = actionService.executeSystemAction(system, action, comment);
            notificationService.sendRegistrationNotification(user, outcome);
        }
    }

    private <T extends IUniqueEntity> void verifyBackwardCompatibility(Class<T> workflowResourceClass) throws WorkflowConfigurationException {
        try {
            entityService.list(workflowResourceClass);
        } catch (IllegalArgumentException e) {
            throw new WorkflowConfigurationException("You attempted to remove an entity of type " + workflowResourceClass.getSimpleName()
                    + " which is required for backward compatibility by the workflow engine", e);
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
