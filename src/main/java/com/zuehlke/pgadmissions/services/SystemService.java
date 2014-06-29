package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ActionRedaction;
import com.zuehlke.pgadmissions.domain.Configuration;
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
import com.zuehlke.pgadmissions.domain.StateActionEnhancement;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedaction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;
import com.zuehlke.pgadmissions.mail.MailService;

@Service
@Transactional(timeout = 60)
public class SystemService {

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
    private ConfigurationService configurationService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private MailService mailService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private StateService stateService;

    @Autowired
    private UserService userService;

    public System getSystem() {
        return entityService.getByProperty(System.class, "name", systemName);
    }

    public System getOrCreateSystem(User systemUser) {
        State systemRunning = stateService.getById(PrismState.SYSTEM_APPROVED);
        System transientSystem = new System().withName(systemName).withUser(systemUser).withState(systemRunning);
        return entityService.getOrCreate(transientSystem);
    }

    public Scope getScope(PrismScope scopeId) {
        return entityService.getByProperty(Scope.class, "id", scopeId);
    }

    public StateDuration getStateDuration(State state) {
        return stateService.getStateDuration(getSystem(), state);
    }

    public List<Scope> getScopes() {
        return entityService.getAll(Scope.class);
    }

    public List<Configuration> getConfigurations() {
        return configurationService.getConfigurations(getSystem());
    }

    public WorkflowGraph initialiseSystem() {
        initialiseScopes();
        initialiseRoles();
        initialiseActions();
        initialiseStates();

        User systemUser = userService.getOrCreateUser(systemUserFirstName, systemUserLastName, systemUserEmail);
        System system = getOrCreateSystem(systemUser);

        Role systemRole = roleService.getById(PrismRole.SYSTEM_ADMINISTRATOR);
        roleService.getOrCreateUserRole(system, systemUser, systemRole);

        initialiseConfigurations(system);
        initialiseNotificationTemplates(system);
        initialiseStateDurations(system);

//        WorkflowGraph workflow = initialiseStateActions();

        if (systemUser.getUserAccount() == null) {
            mailService.sendEmailNotification(systemUser, system, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
        }

        entityService.flush();
//        return workflow;
        return null;
    }

    private void initialiseScopes() {
        for (PrismScope prismScope : PrismScope.values()) {
            Scope transientScope = new Scope().withId(prismScope).withPrecedence(prismScope.getPrecedence());
            entityService.getOrCreate(transientScope);
        }
    }

    private void initialiseRoles() {
        Set<Role> rolesWithExclusions = Sets.newHashSet();

        for (PrismRole prismRole : PrismRole.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismRole.getScope());
            Role transientRole = new Role().withId(prismRole).withScope(scope);
            Role role = entityService.getOrCreate(transientRole);
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
        for (PrismAction prismAction : PrismAction.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismAction.getScope());
            Action transientAction = new Action().withId(prismAction).withActionType(prismAction.getActionType()).withScope(scope);
            Action action = entityService.getOrCreate(transientAction);
            action.getRedactions().clear();

            List<PrismActionRedaction> prismActionRedactions = prismAction.getRedactions();

            for (PrismActionRedaction prismActionRedaction : prismActionRedactions) {
                Role role = roleService.getById(prismActionRedaction.getRole());
                ActionRedaction transientActionRedaction = new ActionRedaction().withAction(action).withRole(role)
                        .withRedactionType(prismActionRedaction.getRedactionType());
                ActionRedaction actionRedaction = entityService.getOrCreate(transientActionRedaction);
                action.getRedactions().add(actionRedaction);
            }
        }
    }

    private void initialiseStates() {
        for (PrismState prismState : PrismState.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismState.getScope());
            State transientState = new State().withId(prismState).withSequenceOrder(prismState.getSequenceOrder()).withScope(scope);
            entityService.getOrCreate(transientState);
        }
        for (PrismState prismState : PrismState.values()) {
            State childState = stateService.getById(prismState);
            State parentState = stateService.getById(PrismState.getParentState(prismState));
            childState.setParentState(parentState);
        }
    }

    private void initialiseConfigurations(System system) {
        for (PrismConfiguration prismConfiguration : PrismConfiguration.values()) {
            Configuration transientConfiguration = new Configuration().withSystem(system).withParameter(prismConfiguration)
                    .withValue(prismConfiguration.getDefaultValue());
            entityService.getOrCreate(transientConfiguration);
        }
    }

    private void initialiseNotificationTemplates(System system) {
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
                String defaultSubject = getFileContent(EMAIL_DEFAULT_SUBJECT_DIRECTORY + prismTemplate.getInitialTemplateSubject());
                String defaultContent = getFileContent(EMAIL_DEFAULT_CONTENT_DIRECTORY + prismTemplate.getInitialTemplateContent());
                version = new NotificationTemplateVersion().withNotificationTemplate(template).withSubject(defaultSubject).withContent(defaultContent)
                        .withCreatedTimestamp(new DateTime());
                entityService.save(version);
            } else {
                template = duplicateTemplate;
                version = notificationService.getActiveVersion(system, template);
                if (version == null) {
                    version = notificationService.getLatestVersion(system, template);
                }
            }

            createdTemplates.put(template, version);
        }

        for (NotificationTemplate template : createdTemplates.keySet()) {
            template.setReminderTemplate(notificationService.getById(PrismNotificationTemplate.getReminderTemplate(template.getId())));
            NotificationConfiguration transientConfiguration = new NotificationConfiguration().withSystem(system).withNotificationTemplate(template)
                    .withNotificationTemplateVersion(createdTemplates.get(template))
                    .withReminderInterval(PrismNotificationTemplate.getReminderInterval(template.getId()));
            entityService.getOrCreate(transientConfiguration);
        }
    }

    private String getFileContent(String filePath) {
        try {
            return Joiner.on(java.lang.System.lineSeparator()).join(Resources.readLines(Resources.getResource(filePath), Charsets.UTF_8));
        } catch (IOException e) {
            throw new Error("Could not access default notification template", e);
        }
    }

    private void initialiseStateDurations(System system) {
        for (PrismState prismState : PrismState.values()) {
            if (prismState.getDuration() != null) {
                State state = stateService.getById(prismState);
                StateDuration transientStateDuration = new StateDuration().withSystem(system).withState(state).withDuration(prismState.getDuration());
                entityService.getOrCreate(transientStateDuration);
            }
        }
    }

    private WorkflowGraph initialiseStateActions() {
        WorkflowGraph workflow = new WorkflowGraph();

        if (stateService.getPendingStateTransitions().size() == 0) {
            stateService.deleteStateActions();

            for (State state : stateService.getStates()) {
                for (PrismStateAction prismStateAction : state.getId().getStateActions()) {
                    Action action = actionService.getById(prismStateAction.getAction());
                    NotificationTemplate template = notificationService.getById(prismStateAction.getNotificationTemplate());
                    StateAction transientStateAction = new StateAction().withState(state).withAction(action)
                            .withRaisesUrgentFlag(prismStateAction.isRaisesUrgentFlag()).withDefaultAction(prismStateAction.isDefaultAction())
                            .withNotificationTemplate(template);
                    StateAction stateAction = entityService.getOrCreate(transientStateAction);
                    initialiseStateActionAssignments(prismStateAction, stateAction);
                    initialiseStateActionNotifications(prismStateAction, stateAction);
                    initialiseStateTransitions(prismStateAction, stateAction, workflow);
                }
            }

            stateService.deleteObseleteStateDurations();
            notificationService.deleteObseleteNotificationConfigurations();

            reassignResourceStates();
        } else {
            try {
                stateService.executePropagatedStateTransitions();
                Thread.sleep(100);
                workflow = initialiseStateActions();
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }
        return workflow;
    }

    private void initialiseStateActionAssignments(PrismStateAction prismStateAction, StateAction stateAction) {
        for (PrismStateActionAssignment prismAssignment : prismStateAction.getAssignments()) {
            Role role = roleService.getById(prismAssignment.getRole());
            StateActionAssignment transientAssignment = new StateActionAssignment().withStateAction(stateAction).withRole(role);
            StateActionAssignment assignment = entityService.getOrCreate(transientAssignment);
            stateAction.getStateActionAssignments().add(assignment);
            initialiseStateActionEnhancements(prismAssignment, assignment);
        }
    }

    private void initialiseStateActionEnhancements(PrismStateActionAssignment prismAssignment, StateActionAssignment assignment) {
        for (PrismStateActionEnhancement prismEnhancement : prismAssignment.getEnhancements()) {
            Action delegatedAction = actionService.getById(prismEnhancement.getDelegatedAction());
            StateActionEnhancement transientEnhancement = new StateActionEnhancement().withStateActionAssignment(assignment)
                    .withEnhancementType(prismEnhancement.getEnhancement()).withDelegatedAction(delegatedAction);
            StateActionEnhancement enhancement = entityService.getOrCreate(transientEnhancement);
            assignment.getEnhancements().add(enhancement);
        }
    }

    private void initialiseStateActionNotifications(PrismStateAction prismStateAction, StateAction stateAction) {
        for (PrismStateActionNotification prismNotification : prismStateAction.getNotifications()) {
            Role role = roleService.getById(prismNotification.getRole());
            NotificationTemplate template = notificationService.getById(prismNotification.getTemplate());
            StateActionNotification transientNotification = new StateActionNotification().withStateAction(stateAction).withRole(role)
                    .withNotificationTemplate(template);
            StateActionNotification notification = entityService.getOrCreate(transientNotification);
            stateAction.getStateActionNotifications().add(notification);
        }
    }

    private void initialiseStateTransitions(PrismStateAction prismStateAction, StateAction stateAction, WorkflowGraph workflow) {
        for (PrismStateTransition prismStateTransition : prismStateAction.getTransitions()) {
            State transitionState = stateService.getById(prismStateTransition.getTransitionState());
            Action transitionAction = actionService.getById(prismStateTransition.getTransitionAction());
            PrismTransitionEvaluation transitionEvaluation = prismStateTransition.getEvaluation();
            StateTransition transientStateTransition = new StateTransition().withStateAction(stateAction).withTransitionState(transitionState)
                    .withTransitionAction(transitionAction).withStateTransitionEvaluation(transitionEvaluation)
                    .withDoPostComment(prismStateTransition.isPostComment());
            StateTransition stateTransition = entityService.getOrCreate(transientStateTransition);
            stateAction.getStateTransitions().add(stateTransition);
            initialiseRoleTransitions(prismStateTransition, stateTransition);

            Set<Action> propagatedActions = stateTransition.getPropagatedActions();
            for (PrismAction prismAction : prismStateTransition.getPropagatedActions()) {
                Action action = actionService.getById(prismAction);
                propagatedActions.add(action);
            }

            workflow.createOrUpdateNode(stateAction.getState().getId(), transitionState.getId(), transitionEvaluation);
        }
    }

    private void initialiseRoleTransitions(PrismStateTransition prismStateTransition, StateTransition stateTransition) {
        for (PrismRoleTransition prismRoleTransition : prismStateTransition.getRoleTransitions()) {
            Role role = roleService.getById(prismRoleTransition.getRole());
            Role transitionRole = roleService.getById(prismRoleTransition.getTransitionRole());
            RoleTransition transientRoleTransition = new RoleTransition().withStateTransition(stateTransition).withRole(role)
                    .withRoleTransitionType(prismRoleTransition.getTransitionType()).withTransitionRole(transitionRole)
                    .withRestrictToActionOwner(prismRoleTransition.isRestrictToActionOwner()).withMinimumPermitted(prismRoleTransition.getMinimumPermitted())
                    .withMaximumPermitted(prismRoleTransition.getMaximumPermitted());
            RoleTransition roleTransition = entityService.getOrCreate(transientRoleTransition);
            stateTransition.getRoleTransitions().add(roleTransition);
        }
    }

    private void reassignResourceStates() {
        for (Scope scope : getScopes()) {
            Class<? extends Resource> resourceClass = scope.getId().getResourceClass();
            for (State state : stateService.getDeprecatedStates(resourceClass)) {
                State degradationState = stateService.getDegradationState(state);
                resourceService.reassignState(resourceClass, state, degradationState);
            }
        }
    }

    private class WorkflowGraph {

        private final HashMap<PrismState, WorkflowNode> nodes = Maps.newHashMap();

        public void createOrUpdateNode(PrismState stateId, PrismState transitionStateId, PrismTransitionEvaluation stateTransitionEvaluation) {
            WorkflowNode node = getWorkflowNode(stateId);
            WorkflowNode inverseNode = getWorkflowNode(transitionStateId);

            if (node == null) {
                node = nodes.put(stateId, new WorkflowNode());
            }

            if (inverseNode == null) {
                inverseNode = nodes.put(transitionStateId, new WorkflowNode());
            }

            node.addOutgoingEdge(stateId, stateTransitionEvaluation);
            inverseNode.addIncomingEdge(stateId, stateTransitionEvaluation);
        }

        public WorkflowNode getWorkflowNode(PrismState stateId) {
            return nodes.get(stateId);
        }

        private class WorkflowNode {

            private HashMap<PrismState, PrismTransitionEvaluation> incomingEdges = Maps.newHashMap();

            private HashMap<PrismState, PrismTransitionEvaluation> outgoingEdges = Maps.newHashMap();

            public HashMap<PrismState, PrismTransitionEvaluation> getIncomingEdges() {
                return incomingEdges;
            }

            public void addIncomingEdge(PrismState transitionStateId, PrismTransitionEvaluation transitionEvaluation) {
                incomingEdges.put(transitionStateId, transitionEvaluation);
            }

            public HashMap<PrismState, PrismTransitionEvaluation> getOutgoingEdges() {
                return outgoingEdges;
            }

            public void addOutgoingEdge(PrismState transitionStateId, PrismTransitionEvaluation transitionEvaluation) {
                outgoingEdges.put(transitionStateId, transitionEvaluation);
            }



        }

    }

}
