package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.DISPLAY_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.STATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.WORKFLOW_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_INITIALIZED_SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DESCRIPTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getSystemOpportunityType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.SYSTEM_RUNNING;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedaction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PrismReminderDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTermination;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.workflow.ActionRedaction;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.workflow.StateActionNotification;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.domain.workflow.StateTermination;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SocialMetadataDTO;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO.StateDurationConfigurationValueDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.FileUtils;

@Service
public class SystemService {

    private static final Logger logger = LoggerFactory.getLogger(SystemService.class);

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${system.id}")
    private Integer systemId;

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
    private CustomizationService customizationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private StateService stateService;

    @Autowired
    private UserService userService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private ApplicationContext applicationContext;

    @Transactional
    public System getSystem() {
        return entityService.getByProperty(System.class, "title", systemName);
    }

    @Transactional
    public void destroyDisplayProperties() {
        logger.info("Destroying default display properties");
        entityService.deleteAll(DisplayPropertyConfiguration.class);
        entityService.deleteAll(DisplayPropertyDefinition.class);
    }

    @Transactional(timeout = 600)
    public void initializeDisplayProperties() throws Exception {
        logger.info("Initializing display property definitions");
        verifyBackwardCompatibility(DisplayPropertyDefinition.class);
        initializeDisplayPropertyDefinitions();

        logger.info("Initializing display property configurations");
        initializeDisplayPropertyConfigurations(getSystem());
    }

    @Transactional(timeout = 600)
    public void initializeWorkflow() throws Exception {
        logger.info("Initializing scope definitions");
        verifyBackwardCompatibility(Scope.class);
        initializeScopeDefinitions();

        logger.info("Initializing role definitions");
        verifyBackwardCompatibility(Role.class);
        initializeRoles();

        logger.info("Initializing action custom question definitions");
        verifyBackwardCompatibility(ActionCustomQuestionDefinition.class);
        initializeActionCustomQuestionDefinitions();

        logger.info("Initializing action definitions");
        verifyBackwardCompatibility(Action.class);
        initializeActions();

        logger.info("Initializing state group definitions");
        verifyBackwardCompatibility(StateGroup.class);
        initializeStateGroups();

        logger.info("Initializing state definitions");
        verifyBackwardCompatibility(State.class);
        initializeStates();

        logger.info("Initializing state duration definitions");
        verifyBackwardCompatibility(StateDurationDefinition.class);
        initializeStateDurationDefinitions();

        logger.info("Initializing workflow property definitions");
        verifyBackwardCompatibility(WorkflowPropertyDefinition.class);
        initializeWorkflowPropertyDefinitions();

        logger.info("Initializing notification definitions");
        verifyBackwardCompatibility(NotificationDefinition.class);
        initializeNotificationDefinitions();

        logger.info("Initializing state action definitions");
        initializeStateActions();

        logger.info("Initializing system object");
        System system = initializeSystemResource();

        logger.info("Initializing state duration configurations");
        initializeStateDurationConfigurations(system);

        logger.info("Initializing workflow property configurations");
        initializeWorkflowPropertyConfigurations(system);

        logger.info("Initializing notification configurations");
        initializeNotificationConfigurations(system);

        entityService.flush();
        entityService.clear();
    }

    @Transactional
    public void initializeSystemUser() throws Exception {
        System system = getSystem();
        User user = system.getUser();
        if (user.getUserAccount() == null) {
            Action action = actionService.getById(SYSTEM_STARTUP);
            String content = applicationContext.getBean(PropertyLoader.class).localize(system).load(SYSTEM_COMMENT_INITIALIZED_SYSTEM);
            Comment comment = new Comment().withAction(action).withContent(content).withDeclinedResponse(false).withUser(user)
                    .withCreatedTimestamp(new DateTime()).addAssignedUser(user, roleService.getCreatorRole(system), CREATE);
            ActionOutcomeDTO outcome = actionService.executeAction(system, action, comment);
            notificationService.sendRegistrationNotification(user, outcome, comment);
        }
    }

    @Transactional
    public void setLastDataImportDate(LocalDate baseline) {
        getSystem().setLastDataImportDate(baseline);
    }

    @Transactional
    public void setLastNotifiedRecommendationSyndicated(LocalDate baseline) {
        getSystem().setLastNotifiedRecommendationSyndicated(baseline);
    }

    @Transactional
    public SocialMetadataDTO getSocialMetadata() throws Exception {
        System system = getSystem();
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(system);
        return new SocialMetadataDTO().withAuthor(system.getUser().getFullName()).withTitle(system.getTitle())
                .withDescription(loader.load(SYSTEM_DESCRIPTION))
                .withThumbnailUrl(resourceService.getSocialThumbnailUrl(system)).withResourceUrl(resourceService.getSocialResourceUrl(system));
    }

    @Transactional
    public SearchEngineAdvertDTO getSearchEngineAdvert() {
        return new SearchEngineAdvertDTO().withRelatedInstitutions(institutionService.getActiveInstitions());
    }

    @Transactional
    public AWSCredentials getAmazonCredentials() throws IntegrationException {
        System system = getSystem();
        String accessKey = system.getAmazonAccessKey();
        String secretKey = system.getAmazonSecretKey();

        if (accessKey == null || secretKey == null) {
            throw new IntegrationException("Amazon credentials not in database");
        }

        return new BasicAWSCredentials(accessKey, secretKey);
    }

    private void initializeScopeDefinitions() throws DeduplicationException {
        for (PrismScope prismScope : PrismScope.values()) {
            entityService.createOrUpdate(new Scope().withId(prismScope).withShortCode(prismScope.getShortCode()).withOrdinal(prismScope.ordinal()));
        }
    }

    private void initializeRoles() throws DeduplicationException {
        for (PrismRole prismRole : PrismRole.values()) {
            Scope scope = scopeService.getById(prismRole.getScope());
            entityService.createOrUpdate(new Role().withId(prismRole).withRoleCategory(prismRole.getRoleCategory()).withScope(scope));
        }
    }

    private void initializeActionCustomQuestionDefinitions() {
        for (PrismActionCustomQuestionDefinition prismActionCustomQuestion : PrismActionCustomQuestionDefinition.values()) {
            Scope scope = scopeService.getById(prismActionCustomQuestion.getScope());
            ActionCustomQuestionDefinition transientActionCustomQuestionDefinition = new ActionCustomQuestionDefinition().withId(prismActionCustomQuestion)
                    .withScope(scope);
            entityService.createOrUpdate(transientActionCustomQuestionDefinition);
        }
    }

    private void initializeActions() throws DeduplicationException {
        entityService.deleteAll(ActionRedaction.class);

        for (PrismAction prismAction : PrismAction.values()) {
            Scope scope = scopeService.getById(prismAction.getScope());
            ActionCustomQuestionDefinition actionCustomQuestionDefinition = actionService
                    .getCustomQuestionDefinitionById(prismAction.getActionCustomQuestion());
            Action transientAction = new Action().withId(prismAction).withActionType(prismAction.getActionType())
                    .withActionCategory(prismAction.getActionCategory()).withRatingAction(prismAction.isRatingAction())
                    .withDeclinableAction(prismAction.isDeclinableAction()).withVisibleAction(prismAction.isVisibleAction())
                    .withActionCustomQuestionDefinition(actionCustomQuestionDefinition).withScope(scope);
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

    private void initializeStateGroups() throws DeduplicationException {
        for (PrismStateGroup prismStateGroup : PrismStateGroup.values()) {
            Scope scope = scopeService.getById(prismStateGroup.getScope());
            StateGroup transientStateGroup = new StateGroup().withId(prismStateGroup).withSequenceOrder(prismStateGroup.ordinal()).withScope(scope);
            entityService.createOrUpdate(transientStateGroup);
        }
    }

    private void initializeStates() throws DeduplicationException {
        for (PrismState prismState : PrismState.values()) {
            StateDurationDefinition stateDurationDefinition = stateService.getStateDurationDefinitionById(prismState.getDefaultDuration());
            Scope scope = entityService.getByProperty(Scope.class, "id", prismState.getStateGroup().getScope());
            StateGroup stateGroup = entityService.getByProperty(StateGroup.class, "id", prismState.getStateGroup());
            State transientState = new State().withId(prismState).withStateGroup(stateGroup).withStateDurationDefinition(stateDurationDefinition)
                    .withStateDurationEvaluation(prismState.getStateDurationEvaluation()).withScope(scope);
            entityService.createOrUpdate(transientState);
        }
    }

    private void initializeStateDurationDefinitions() throws DeduplicationException {
        for (PrismStateDurationDefinition prismStateDuration : PrismStateDurationDefinition.values()) {
            Scope scope = scopeService.getById(prismStateDuration.getScope());
            StateDurationDefinition transientStateDurationDefinition = new StateDurationDefinition().withId(prismStateDuration)
                    .withEscalation(prismStateDuration.isEscalation()).withScope(scope);
            entityService.createOrUpdate(transientStateDurationDefinition);
        }
    }

    private void initializeNotificationDefinitions() throws DeduplicationException, CustomizationException {
        HashMap<PrismNotificationDefinition, NotificationDefinition> definitions = Maps.newHashMap();
        for (PrismNotificationDefinition prismNotificationDefinition : PrismNotificationDefinition.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismNotificationDefinition.getScope());
            NotificationDefinition transientNotificationDefinition = new NotificationDefinition().withId(prismNotificationDefinition)
                    .withNotificationType(prismNotificationDefinition.getNotificationType())
                    .withNotificationPurpose(prismNotificationDefinition.getNotificationPurpose()).withScope(scope);
            NotificationDefinition persistentNotificationTemplateDefinition = entityService.createOrUpdate(transientNotificationDefinition);
            definitions.put(prismNotificationDefinition, persistentNotificationTemplateDefinition);
        }
        HashMap<PrismNotificationDefinition, PrismReminderDefinition> requestReminderDefinitions = PrismNotificationDefinition.getReminderDefinitions();
        for (PrismNotificationDefinition prismRequestDefinition : requestReminderDefinitions.keySet()) {
            NotificationDefinition requestDefinition = definitions.get(prismRequestDefinition);
            NotificationDefinition reminderDefinition = definitions.get(prismRequestDefinition.getReminderDefinition());
            requestDefinition.setReminderDefinition(reminderDefinition);
        }
    }

    private void initializeDisplayPropertyDefinitions() throws DeduplicationException {
        for (PrismDisplayPropertyDefinition prismDisplayPropertyDefinition : PrismDisplayPropertyDefinition.values()) {
            Scope scope = scopeService.getById(prismDisplayPropertyDefinition.getCategory().getScope());
            entityService.createOrUpdate(new DisplayPropertyDefinition().withId(prismDisplayPropertyDefinition)
                    .withCategory(prismDisplayPropertyDefinition.getCategory()).withScope(scope));
        }
    }

    private void initializeWorkflowPropertyDefinitions() {
        for (PrismWorkflowPropertyDefinition prismWorkflowPropertyDefinition : PrismWorkflowPropertyDefinition.values()) {
            Scope scope = scopeService.getById(prismWorkflowPropertyDefinition.getScope());
            WorkflowPropertyDefinition transientWorkflowPropertyDefinition = new WorkflowPropertyDefinition().withId(prismWorkflowPropertyDefinition)
                    .withCategory(prismWorkflowPropertyDefinition.getCategory()).withDefineRange(prismWorkflowPropertyDefinition.isDefineRange())
                    .withCanBeDisabled(prismWorkflowPropertyDefinition.isCanBeDisabled()).withCanBeOptional(prismWorkflowPropertyDefinition.isCanBeOptional())
                    .withMinimumPermitted(prismWorkflowPropertyDefinition.getMinimumPermitted())
                    .withMaximumPermitted(prismWorkflowPropertyDefinition.getMaximumPermitted())
                    .withScope(scope);
            entityService.createOrUpdate(transientWorkflowPropertyDefinition);
        }
    }

    private System initializeSystemResource() throws DeduplicationException {
        System system = getSystem();
        User systemUser = userService.getOrCreateUser(systemUserFirstName, systemUserLastName, systemUserEmail);
        DateTime baseline = new DateTime();

        if (system == null) {
            State systemRunning = stateService.getById(SYSTEM_RUNNING);
            system = new System().withId(systemId).withTitle(systemName).withUser(systemUser).withState(systemRunning)
                    .withCipherSalt(EncryptionUtils.getUUID()).withCreatedTimestamp(baseline).withUpdatedTimestamp(baseline);
            entityService.save(system);

            ResourceState systemState = new ResourceState().withResource(system).withState(systemRunning).withPrimaryState(true);
            entityService.save(systemState);
            system.getResourceStates().add(systemState);
        } else {
            system.setId(systemId);
            system.setTitle(systemName);
            system.setUser(systemUser);
            system.setUpdatedTimestamp(baseline);
        }

        system.setCode(resourceService.generateResourceCode(system));
        return system;
    }

    private void initializeStateDurationConfigurations(System system) throws DeduplicationException, CustomizationException, InstantiationException,
            IllegalAccessException {
        for (PrismScope prismScope : scopeService.getScopesDescending()) {
            StateDurationConfigurationDTO configurationDTO = new StateDurationConfigurationDTO();
            for (PrismStateDurationDefinition prismStateDuration : PrismStateDurationDefinition.values()) {
                if (prismScope == prismStateDuration.getScope()) {
                    configurationDTO.add(new StateDurationConfigurationValueDTO().withDefinitionId(prismStateDuration).withDuration(
                            prismStateDuration.getDefaultDuration()));
                }
            }
            persistConfigurations(STATE_DURATION, system, prismScope, configurationDTO);
        }
    }

    private void initializeDisplayPropertyConfigurations(System system) throws DeduplicationException, CustomizationException, InstantiationException,
            IllegalAccessException {
        for (PrismScope prismScope : scopeService.getScopesDescending()) {
            for (PrismDisplayPropertyDefinition prismDisplayPropertyDefinition : PrismDisplayPropertyDefinition.values()) {
                if (prismScope == prismDisplayPropertyDefinition.getCategory().getScope()) {
                    DisplayPropertyConfigurationDTO configurationDTO = new DisplayPropertyConfigurationDTO().withDefinitionId(prismDisplayPropertyDefinition)
                            .withValue(
                                    prismDisplayPropertyDefinition.getDefaultValue());
                    customizationService.createOrUpdateConfiguration(DISPLAY_PROPERTY, system,
                            prismScope.ordinal() > INSTITUTION.ordinal() ? getSystemOpportunityType() : null, configurationDTO);
                }
            }
        }
    }

    private void initializeWorkflowPropertyConfigurations(System system) throws DeduplicationException, CustomizationException, InstantiationException,
            IllegalAccessException {
        for (PrismScope prismScope : scopeService.getScopesDescending()) {
            WorkflowPropertyConfigurationDTO configurationDTO = new WorkflowPropertyConfigurationDTO();
            for (PrismWorkflowPropertyDefinition prismWorkflowProperty : PrismWorkflowPropertyDefinition.values()) {
                if (prismScope == prismWorkflowProperty.getScope()) {
                    boolean range = prismWorkflowProperty.isDefineRange();

                    Boolean enabled = prismWorkflowProperty.getDefaultEnabled();
                    enabled = enabled == null ? range && prismWorkflowProperty.getDefaultMaximum() > 0 : enabled;

                    Boolean required = prismWorkflowProperty.getDefaultRequired();
                    required = required == null ? range && prismWorkflowProperty.getDefaultMinimum() > 0 : required;

                    configurationDTO.add(new WorkflowPropertyConfigurationValueDTO().withDefinition(prismWorkflowProperty).withEnabled(enabled)
                            .withRequired(required).withMinimum(prismWorkflowProperty.getDefaultMinimum())
                            .withMaximum(prismWorkflowProperty.getDefaultMaximum()));
                }
            }
            persistConfigurations(WORKFLOW_PROPERTY, system, prismScope, configurationDTO);
        }
    }

    private void initializeNotificationConfigurations(System system) throws DeduplicationException, CustomizationException, InstantiationException,
            IllegalAccessException {
        for (PrismNotificationDefinition prismNotificationDefinition : PrismNotificationDefinition.values()) {
            String subject = FileUtils.getContent(defaultEmailSubjectDirectory + prismNotificationDefinition.getInitialTemplateSubject());
            String content = FileUtils.getContent(defaultEmailContentDirectory + prismNotificationDefinition.getInitialTemplateContent());

            PrismOpportunityType opportunityType = prismNotificationDefinition.getScope().ordinal() > INSTITUTION.ordinal() ? PrismOpportunityType
                    .getSystemOpportunityType() : null;

            NotificationConfigurationDTO configurationDTO = new NotificationConfigurationDTO().withId(prismNotificationDefinition).withSubject(subject)
                    .withContent(content).withReminderInterval(prismNotificationDefinition.getDefaultReminderDuration());
            customizationService.createOrUpdateConfiguration(NOTIFICATION, system, opportunityType, configurationDTO);
        }
    }

    private void initializeStateActions() throws DeduplicationException, WorkflowConfigurationException {
        stateService.deleteStateActions();

        for (State state : stateService.getStates()) {
            for (PrismStateAction prismStateAction : PrismState.getStateActions(state.getId())) {
                Action action = actionService.getById(prismStateAction.getAction());
                NotificationDefinition template = notificationService.getById(prismStateAction.getNotification());
                StateAction stateAction = new StateAction().withState(state).withAction(action).withRaisesUrgentFlag(prismStateAction.isRaisesUrgentFlag())
                        .withActionCondition(prismStateAction.getActionCondition()).withActionEnhancement(prismStateAction.getActionEnhancement())
                        .withNotificationDefinition(template);
                entityService.save(stateAction);
                state.getStateActions().add(stateAction);

                initializeStateActionAssignments(prismStateAction, stateAction);
                initializeStateActionNotifications(prismStateAction, stateAction);
                initializeStateTransitions(prismStateAction, stateAction);
            }
        }

        actionService.setCreationActions();
        actionService.setFallbackActions();
        actionService.setStateGroupTransitionActions();

        stateService.setRepeatableStateGroups();
        stateService.setHiddenStates();
        stateService.setParallelizableStates();

        roleService.setCreatorRoles();

        stateService.deleteObsoleteStateDurations();
        notificationService.deleteObsoleteNotificationConfigurations();
        roleService.deleteObseleteUserRoles();
    }

    private void initializeStateActionAssignments(PrismStateAction prismStateAction, StateAction stateAction) {
        for (PrismStateActionAssignment prismAssignment : prismStateAction.getAssignments()) {
            Role role = roleService.getById(prismAssignment.getRole());
            StateActionAssignment assignment = new StateActionAssignment().withStateAction(stateAction).withRole(role)
                    .withActionEnhancement(prismAssignment.getActionEnhancement());
            entityService.save(assignment);
            stateAction.getStateActionAssignments().add(assignment);
        }
    }

    private void initializeStateActionNotifications(PrismStateAction prismStateAction, StateAction stateAction) {
        for (PrismStateActionNotification prismNotification : prismStateAction.getNotifications()) {
            Role role = roleService.getById(prismNotification.getRole());
            NotificationDefinition template = notificationService.getById(prismNotification.getNotification());
            StateActionNotification notification = new StateActionNotification().withStateAction(stateAction).withRole(role)
                    .withNotificationDefinition(template);
            entityService.save(notification);
            stateAction.getStateActionNotifications().add(notification);
        }
    }

    private void initializeStateTransitions(PrismStateAction prismStateAction, StateAction stateAction) {
        List<PrismStateTransition> stateTransitions = prismStateAction.getTransitions();
        if (stateTransitions.isEmpty()) {
            stateTransitions.add(new PrismStateTransition().withTransitionState(stateAction.getState().getId()) //
                    .withTransitionAction(stateAction.getAction().getId()));
        }

        for (PrismStateTransition prismStateTransition : stateTransitions) {
            State transitionState = stateService.getById(prismStateTransition.getTransitionState());
            Action transitionAction = actionService.getById(prismStateTransition.getTransitionAction());

            StateTransitionEvaluation stateTransitionEvaluation = null;
            PrismStateTransitionEvaluation prismStateTransitionEvaluation = prismStateTransition.getTransitionEvaluation();
            if (prismStateTransitionEvaluation != null) {
                stateTransitionEvaluation = stateService.getStateTransitionEvaluationById(prismStateTransitionEvaluation);
                if (stateTransitionEvaluation == null) {
                    Scope scope = scopeService.getById(prismStateTransitionEvaluation.getScope());
                    stateTransitionEvaluation = new StateTransitionEvaluation().withId(prismStateTransitionEvaluation)
                            .withNextStateSelection(prismStateTransitionEvaluation.isNextStateSelection()).withScope(scope);
                    entityService.save(stateTransitionEvaluation);
                }
            }

            StateTransition stateTransition = new StateTransition().withStateAction(stateAction).withTransitionState(transitionState)
                    .withTransitionAction(transitionAction).withStateTransitionEvaluation(stateTransitionEvaluation);
            entityService.save(stateTransition);
            stateAction.getStateTransitions().add(stateTransition);

            initializeRoleTransitions(prismStateTransition, stateTransition);
            initializeStateTerminations(prismStateTransition, stateTransition);
            initializePropagatedActions(prismStateTransition, stateTransition);
        }
    }

    private void initializeRoleTransitions(PrismStateTransition prismStateTransition, StateTransition stateTransition) {
        for (PrismRoleTransition prismRoleTransition : prismStateTransition.getRoleTransitions()) {
            Role role = roleService.getById(prismRoleTransition.getRole());
            Role transitionRole = roleService.getById(prismRoleTransition.getTransitionRole());
            WorkflowPropertyDefinition workflowPropertyDefinition = (WorkflowPropertyDefinition) customizationService.getDefinitionById(
                    WORKFLOW_PROPERTY, prismRoleTransition.getPropertyDefinition());
            RoleTransition roleTransition = new RoleTransition().withStateTransition(stateTransition).withRole(role)
                    .withRoleTransitionType(prismRoleTransition.getTransitionType()).withTransitionRole(transitionRole)
                    .withRestrictToActionOwner(prismRoleTransition.getRestrictToActionOwner()).withMinimumPermitted(prismRoleTransition.getMinimumPermitted())
                    .withMaximumPermitted(prismRoleTransition.getMaximumPermitted()).withWorkflowPropertyDefinition(workflowPropertyDefinition);
            entityService.save(roleTransition);
            stateTransition.getRoleTransitions().add(roleTransition);
        }
    }

    private void initializeStateTerminations(PrismStateTransition prismStateTransition, StateTransition stateTransition) {
        for (PrismStateTermination prismStateTermination : prismStateTransition.getStateTerminations()) {
            State state = stateService.getById(prismStateTermination.getTerminationState());
            StateTermination stateTermination = new StateTermination().withStateTransition(stateTransition).withTerminationState(state)
                    .withStateTerminationEvaluation(prismStateTermination.getStateTerminationEvaluation());
            entityService.save(stateTermination);
            stateTransition.getStateTerminations().add(stateTermination);
        }
    }

    private void initializePropagatedActions(PrismStateTransition prismStateTransition, StateTransition stateTransition) {
        Set<Action> propagatedActions = stateTransition.getPropagatedActions();
        for (PrismAction prismAction : prismStateTransition.getPropagatedActions()) {
            Action action = actionService.getById(prismAction);
            propagatedActions.add(action);
        }
    }

    private void persistConfigurations(PrismConfiguration configurationType, System system, PrismScope prismScope,
            List<? extends WorkflowConfigurationDTO> configurationDTO) throws CustomizationException, DeduplicationException, InstantiationException,
            IllegalAccessException {
        if (configurationDTO.size() > 0) {
            customizationService.createConfigurationGroup(configurationType, system, prismScope,
                    prismScope.ordinal() > INSTITUTION.ordinal() ? getSystemOpportunityType() : null, configurationDTO);
        }
    }

    private <T extends UniqueEntity> void verifyBackwardCompatibility(Class<T> workflowResourceClass) throws WorkflowConfigurationException {
        try {
            entityService.list(workflowResourceClass);
        } catch (IllegalArgumentException e) {
            throw new WorkflowConfigurationException(workflowResourceClass.getSimpleName() + " required for backward compatibility", e);
        }
    }

}
