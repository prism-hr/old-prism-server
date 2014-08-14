package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ActionRedaction;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateGroup;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedaction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.services.exporters.ApplicationExportService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
public class SystemService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    private EncryptionUtils encryptionUtils;

    @Autowired
    private EntityService entityService;

    @Autowired
    private EntityImportService entityImportService;

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
    private ApplicationExportService applicationExportService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public System getSystem() {
        return entityService.getByProperty(System.class, "name", systemName);
    }

    @Transactional(timeout = 120)
    public void initialiseSystem() throws WorkflowConfigurationException, WorkflowEngineException {
        logger.info("Initialising scope definitions");
        verifyBackwardCompatibility(Scope.class);
        initialiseScopes();

        logger.info("Initialising role definitions");
        verifyBackwardCompatibility(Role.class);
        initialiseRoles();

        logger.info("Initialising action definitions");
        verifyBackwardCompatibility(Action.class);
        initialiseActions();

        logger.info("Initialising state group definitions");
        verifyBackwardCompatibility(StateGroup.class);
        initialiseStateGroups();

        logger.info("Initialising state definitions");
        verifyBackwardCompatibility(State.class);
        initialiseStates();

        logger.info("Initialising system");
        System system = initialiseSystemResource();

        logger.info("Initialising fallback action definititions");
        initialiseFallbackActions();

        logger.info("Initialising notification definitions");
        initialiseNotificationTemplates(system);

        logger.info("Initialising state duration definitions");
        initialiseStateDurations(system);

        logger.info("Initialising workflow definitions");
        initialiseStateActions();

        logger.info("Initialising system user");
        initialiseSystemUser(system);

        entityService.flush();
        entityService.clear();
    }

    @Transactional
    public void initializeSearchIndexes() {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        fullTextSession.createIndexer().start();
    }

    @Scheduled(cron = "${maintenance.ongoing}")
    public void maintainSystem() {
        try {
            logger.info("Executing pending state transitions");
            stateService.executePendingStateTransitions();
        } catch (Exception e) {
            logger.info("Error executing pending state transitions", e);
        }

        if (!stateService.hasPendingStateTransitions()) {
            try {
                logger.info("Importing reference data");
                entityImportService.importReferenceData();
            } catch (Exception e) {
                logger.info("Error importing reference data", e);
            }

            try {
                logger.trace("Exporting applications");
                applicationExportService.exportUclApplications();
            } catch (Exception e) {
                logger.info("Error exporting applications", e);
            }

            try {
                logger.info("Sending deferred workflow notifications.");
                notificationService.sendDeferredWorkflowNotifications();
            } catch (Exception e) {
                logger.info("Error sending deferred workflow notifications", e);
            }
        }

        try {
            logger.info("Deleting unused documents");
            documentService.deleteOrphanDocuments();
        } catch (Exception e) {
            logger.info("Error deleting unused documents", e);
        }
    }

    private void initialiseScopes() {
        for (PrismScope prismScope : PrismScope.values()) {
            Scope transientScope = new Scope().withId(prismScope).withPrecedence(prismScope.getPrecedence()).withShortCode(prismScope.getShortCode());
            entityService.createOrUpdate(transientScope);
        }
    }

    private void initialiseRoles() {
        roleService.deleteExludedRoles();
        Set<Role> rolesWithExclusions = Sets.newHashSet();

        for (PrismRole prismRole : PrismRole.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismRole.getScope());
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

    private void initialiseActions() {
        entityService.deleteAll(ActionRedaction.class);

        for (PrismAction prismAction : PrismAction.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismAction.getScope());
            Scope creationScope = entityService.getByProperty(Scope.class, "id", prismAction.getCreationScope());
            Action transientAction = new Action().withId(prismAction).withActionType(prismAction.getActionType())
                    .withActionCategory(prismAction.getActionCategory()).withScope(scope).withCreationScope(creationScope);
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
    }

    private void initialiseStateGroups() {
        for (PrismStateGroup prismStateGroup : PrismStateGroup.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismStateGroup.getScope());
            StateGroup transientStateGroup = new StateGroup().withId(prismStateGroup).withSequenceOrder(prismStateGroup.getSequenceOrder()).withScope(scope);
            entityService.createOrUpdate(transientStateGroup);
        }
    }

    private void initialiseStates() {
        for (PrismState prismState : PrismState.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismState.getScope());
            StateGroup stateGroup = entityService.getByProperty(StateGroup.class, "id", prismState.getStateGroup());
            State transientState = new State().withId(prismState).withStateGroup(stateGroup).withScope(scope);
            entityService.createOrUpdate(transientState);
        }
    }

    private System initialiseSystemResource() {
        User systemUser = userService.getOrCreateUser(systemUserFirstName, systemUserLastName, systemUserEmail);
        State systemRunning = stateService.getById(PrismState.SYSTEM_RUNNING);
        DateTime startupTimestamp = new DateTime();
        System transientSystem = new System().withName(systemName).withUser(systemUser).withState(systemRunning).withCreatedTimestamp(startupTimestamp)
                .withUpdatedTimestamp(startupTimestamp);
        System system = entityService.createOrUpdate(transientSystem);
        system.setCode(resourceService.generateResoureCode(system));
        return system;
    }

    private void initialiseFallbackActions() {
        for (Scope scope : scopeService.getScopesAscending()) {
            Action fallbackAction = actionService.getById(PrismScope.getFallbackAction(scope.getId()));
            scope.setFallbackAction(fallbackAction);
        }
    }

    private void initialiseNotificationTemplates(System system) {
        if (BooleanUtils.isTrue(initializeNotifications)) {
            notificationService.deleteAllNotifications();
        }

        HashMap<NotificationTemplate, NotificationTemplateVersion> createdTemplates = Maps.newHashMap();
        for (PrismNotificationTemplate prismTemplate : PrismNotificationTemplate.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismTemplate.getScope());

            NotificationTemplate template;
            NotificationTemplate transientTemplate = new NotificationTemplate().withId(prismTemplate).withNotificationType(prismTemplate.getNotificationType())
                    .withNotificationPurpose(prismTemplate.getNotificationPurpose()).withScope(scope);
            NotificationTemplate duplicateTemplate = entityService.getDuplicateEntity(transientTemplate);
            NotificationTemplateVersion version;

            if (duplicateTemplate == null) {
                entityService.save(transientTemplate);
                template = transientTemplate;
                version = initialiseNotificationTemplateVersion(template);
            } else {
                template = duplicateTemplate;
                version = notificationService.getActiveVersion(system, template);
                if (version == null) {
                    version = initialiseNotificationTemplateVersion(duplicateTemplate);
                }
            }

            createdTemplates.put(template, version);
        }

        for (NotificationTemplate template : createdTemplates.keySet()) {
            template.setReminderTemplate(notificationService.getById(PrismNotificationTemplate.getReminderTemplate(template.getId())));
            NotificationConfiguration transientConfiguration = new NotificationConfiguration().withSystem(system).withNotificationTemplate(template)
                    .withNotificationTemplateVersion(createdTemplates.get(template))
                    .withReminderInterval(PrismNotificationTemplate.getReminderInterval(template.getId()));
            entityService.createOrUpdate(transientConfiguration);
        }
    }

    private NotificationTemplateVersion initialiseNotificationTemplateVersion(NotificationTemplate template) {
        PrismNotificationTemplate templateId = template.getId();
        String defaultSubject = getFileContent(defaultEmailSubjectDirectory + templateId.getInitialTemplateSubject());
        String defaultContent = getFileContent(defaultEmailContentDirectory + templateId.getInitialTemplateContent());
        NotificationTemplateVersion version = new NotificationTemplateVersion().withNotificationTemplate(template).withSubject(defaultSubject)
                .withContent(defaultContent).withCreatedTimestamp(new DateTime());
        entityService.save(version);
        return version;
    }

    private void initialiseStateDurations(System system) {
        for (PrismState prismState : PrismState.values()) {
            if (prismState.getDuration() != null) {
                State state = stateService.getById(prismState);
                StateDuration transientStateDuration = new StateDuration().withSystem(system).withState(state).withDuration(prismState.getDuration());
                entityService.createOrUpdate(transientStateDuration);
            }
        }
    }

    private void initialiseStateActions() throws WorkflowConfigurationException {
        if (!stateService.hasPendingStateTransitions()) {
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

            stateService.deleteObseleteStateDurations();
            notificationService.deleteObseleteNotificationConfigurations();
            roleService.deleteInactiveRoles();

            verifyBackwardResourceCompatibility();
        } else {
            try {
                stateService.executePendingStateTransitions();
                Thread.sleep(100);
                initialiseStateActions();
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }
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
            PrismTransitionEvaluation transitionEvaluation = prismStateTransition.getTransitionEvaluation();
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

    private void initialiseSystemUser(System system) throws WorkflowEngineException {
        User user = system.getUser();
        if (user.getUserAccount() == null) {
            Action action = actionService.getById(PrismAction.SYSTEM_STARTUP);
            Comment comment = new Comment().withUser(user).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                    .withAssignedUser(user, roleService.getCreatorRole(system));
            actionService.executeUserAction(system, action, comment);
            notificationService.sendNotification(user, system, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
        }
    }

    private <T extends IUniqueEntity> void verifyBackwardCompatibility(Class<T> workflowResourceClass) throws WorkflowConfigurationException {
        try {
            entityService.list(workflowResourceClass);
        } catch (IllegalArgumentException e) {
            throw new WorkflowConfigurationException(e);
        }
    }

    private void verifyBackwardResourceCompatibility() throws WorkflowConfigurationException {
        for (Scope scope : scopeService.getScopesDescending()) {
            Class<? extends Resource> resourceClass = scope.getId().getResourceClass();
            if (!stateService.getDeprecatedStates(resourceClass).isEmpty()) {
                throw new WorkflowConfigurationException();
            }
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
