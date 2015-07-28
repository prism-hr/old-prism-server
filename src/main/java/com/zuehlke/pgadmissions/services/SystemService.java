package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.DISPLAY_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.STATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.WORKFLOW_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_INITIALIZED_SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getSystemOpportunityType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.SYSTEM_RUNNING;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;
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
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityType;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.resource.System;
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
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import com.zuehlke.pgadmissions.mapping.ImportedEntityMapper;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO.StateDurationConfigurationValueDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramImportDTO;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.PrismFileUtils;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

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

    @Value("${system.minimum.wage}")
    private BigDecimal systemMinimumWage;

    @Value("${system.default.email.subject.directory}")
    private String defaultEmailSubjectDirectory;

    @Value("${system.default.email.content.directory}")
    private String defaultEmailContentDirectory;

    @Inject
    private ActionService actionService;

    @Inject
    private EntityService entityService;

    @Inject
    private CustomizationService customizationService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private RoleService roleService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Inject
    private DocumentService documentService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private TargetingService targetingService;

    @Inject
    private ImportedEntityMapper importedEntityMapper;

    @Inject
    private ApplicationContext applicationContext;

    @Transactional
    public System getSystem() {
        return entityService.getByProperty(System.class, "name", systemName);
    }

    @Transactional
    public void destroyDisplayProperties() {
        logger.info("Destroying display properties");
        entityService.deleteAll(DisplayPropertyConfiguration.class);
        entityService.deleteAll(DisplayPropertyDefinition.class);
    }

    @Transactional(timeout = 600)
    public void initializeDisplayProperties() throws Exception {
        logger.info("Initializing display property definitions");
        verifyDefinition(DisplayPropertyDefinition.class);
        initializeDisplayPropertyDefinitions();

        logger.info("Initializing display property configurations");
        initializeDisplayPropertyConfigurations(getSystem());
    }

    @Transactional(timeout = 600)
    public void initializeWorkflow() throws Exception {
        logger.info("Initializing scope definitions");
        verifyDefinition(Scope.class);
        initializeScopeDefinitions();

        logger.info("Initializing role definitions");
        verifyDefinition(Role.class);
        initializeRoles();

        logger.info("Initializing action custom question definitions");
        verifyDefinition(ActionCustomQuestionDefinition.class);
        initializeActionCustomQuestionDefinitions();

        logger.info("Initializing action definitions");
        verifyDefinition(Action.class);
        initializeActions();

        logger.info("Initializing state group definitions");
        verifyDefinition(StateGroup.class);
        initializeStateGroups();

        logger.info("Initializing state definitions");
        verifyDefinition(State.class);
        initializeStates();

        logger.info("Initializing state duration definitions");
        verifyDefinition(StateDurationDefinition.class);
        initializeStateDurationDefinitions();

        logger.info("Initializing workflow property definitions");
        verifyDefinition(WorkflowPropertyDefinition.class);
        initializeWorkflowPropertyDefinitions();

        logger.info("Initializing notification definitions");
        verifyDefinition(NotificationDefinition.class);
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
    public void overwriteSystemData() {
        importedEntityService.deleteImportedEntityTypes();
    }

    @Transactional(timeout = 600)
    @SuppressWarnings("unchecked")
    public <T extends ImportedEntityRequest> void initializeSystemData() {
        Map<PrismImportedEntity, List<T>> definitions = Maps.newHashMap();
        for (PrismImportedEntity prismImportedEntity : PrismImportedEntity.values()) {
            ImportedEntityType importedEntityType = entityService.getOrCreate(new ImportedEntityType().withId(prismImportedEntity));

            S3Object importedDataSource = documentService.getImportedDataSource(importedEntityType);
            if (importedDataSource != null) {
                logger.info("Initializing system data for: " + prismImportedEntity.name());
                try (InputStream inputStream = importedDataSource.getObjectContent()) {
                    Class<T> requestClass = (Class<T>) prismImportedEntity.getSystemRequestClass();
                    List<T> representations = importedEntityMapper.getImportedEntityRepresentations(requestClass, inputStream);
                    importedEntityService.mergeImportedEntities(prismImportedEntity, representations);
                    importedEntityType.setLastImportedTimestamp(new DateTime(importedDataSource.getObjectMetadata().getLastModified()));
                    definitions.put(prismImportedEntity, representations);
                } catch (Exception e) {
                    logger.error("Unable to initialize system data for: " + prismImportedEntity.name(), e);
                }
            } else {
                logger.info("Skipped initializing system data for: " + prismImportedEntity.name());
            }
        }

        logger.info("Initializing imported program subject areas");
        targetingService.mergeImportedProgramSubjectAreas((List<ImportedProgramImportDTO>) definitions.get(IMPORTED_PROGRAM));

        logger.info("Initializing imported institution subject areas");
        targetingService.mergeImportedInstitutionSubjectAreas();
    }

    @Transactional
    public void setLastNotifiedRecommendationSyndicated(LocalDate baseline) {
        getSystem().setLastNotifiedRecommendationSyndicated(baseline);
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
            Action transientAction = new Action().withId(prismAction).withSystemInvocationOnly(prismAction.isSystemInvocationOnly())
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
        int ordinal = 0;
        PrismScope lastScope = null;
        for (PrismStateGroup prismStateGroup : PrismStateGroup.values()) {
            PrismScope thisScope = prismStateGroup.getScope();
            if (!Objects.equal(thisScope, lastScope)) {
                ordinal = 0;
                lastScope = thisScope;
            }
            Scope scope = scopeService.getById(prismStateGroup.getScope());
            StateGroup transientStateGroup = new StateGroup().withId(prismStateGroup).withOrdinal(ordinal).withScope(scope);
            entityService.createOrUpdate(transientStateGroup);
            ordinal++;
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

    private void initializeStateDurationDefinitions() {
        for (PrismStateDurationDefinition prismStateDuration : PrismStateDurationDefinition.values()) {
            Scope scope = scopeService.getById(prismStateDuration.getScope());
            StateDurationDefinition transientStateDurationDefinition = new StateDurationDefinition().withId(prismStateDuration)
                    .withEscalation(prismStateDuration.isEscalation()).withScope(scope);
            entityService.createOrUpdate(transientStateDurationDefinition);
        }
    }

    private void initializeNotificationDefinitions() {
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
            system = new System().withId(systemId).withName(systemName).withMinimumWage(systemMinimumWage).withUser(systemUser).withState(systemRunning)
                    .withCipherSalt(EncryptionUtils.getUUID()).withCreatedTimestamp(baseline).withUpdatedTimestamp(baseline);
            entityService.save(system);

            ResourceState systemState = new ResourceState().withResource(system).withState(systemRunning).withPrimaryState(true)
                    .withCreatedDate(new LocalDate());
            entityService.save(systemState);
            system.getResourceStates().add(systemState);
        } else {
            system.setId(systemId);
            system.setName(systemName);
            system.setUser(systemUser);
            system.setUpdatedTimestamp(baseline);
        }

        system.setCode(resourceService.generateResourceCode(system));
        return system;
    }

    private void initializeStateDurationConfigurations(System system) {
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

    private void initializeDisplayPropertyConfigurations(System system) {
        for (PrismScope prismScope : scopeService.getScopesDescending()) {
            for (PrismDisplayPropertyDefinition prismDisplayPropertyDefinition : PrismDisplayPropertyDefinition.values()) {
                if (prismScope == prismDisplayPropertyDefinition.getCategory().getScope()) {
                    DisplayPropertyConfigurationDTO configurationDTO = new DisplayPropertyConfigurationDTO().withDefinitionId(prismDisplayPropertyDefinition)
                            .withValue(
                                    prismDisplayPropertyDefinition.getDefaultValue());
                    customizationService.createOrUpdateConfiguration(DISPLAY_PROPERTY, system,
                            prismScope.ordinal() > DEPARTMENT.ordinal() ? getSystemOpportunityType() : null, configurationDTO);
                }
            }
        }
    }

    private void initializeWorkflowPropertyConfigurations(System system) {
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

    private void initializeNotificationConfigurations(System system) {
        for (PrismNotificationDefinition prismNotificationDefinition : PrismNotificationDefinition.values()) {
            String subject = PrismFileUtils.getContent(defaultEmailSubjectDirectory + prismNotificationDefinition.getInitialTemplateSubject());
            String content = PrismFileUtils.getContent(defaultEmailContentDirectory + prismNotificationDefinition.getInitialTemplateContent());

            PrismOpportunityType opportunityType = prismNotificationDefinition.getScope().ordinal() > DEPARTMENT.ordinal() ? PrismOpportunityType
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
                initializeStateAction(state, action, prismStateAction, true);

                PrismAction prismActionOther = prismStateAction.getActionOther();
                if (prismActionOther != null) {
                    Action actionOther = actionService.getById(prismActionOther);
                    initializeStateAction(state, actionOther, prismStateAction, false);
                }
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
        roleService.deleteObsoleteUserRoles();
    }

    private void initializeStateAction(State state, Action action, PrismStateAction prismStateAction, boolean notify) {
        StateAction stateAction = new StateAction().withState(state).withAction(action).withRaisesUrgentFlag(prismStateAction.isRaisesUrgentFlag())
                .withActionCondition(prismStateAction.getActionCondition()).withActionEnhancement(prismStateAction.getActionEnhancement());

        if (notify) {
            NotificationDefinition notificationDefinition = notificationService.getById(prismStateAction.getNotification());
            stateAction.setNotificationDefinition(notificationDefinition);
        }

        entityService.save(stateAction);
        state.getStateActions().add(stateAction);

        initializeStateActionAssignments(prismStateAction, stateAction);
        initializeStateActionNotifications(prismStateAction, stateAction);
        initializeStateTransitions(prismStateAction, stateAction);
    }

    private void initializeStateActionAssignments(PrismStateAction prismStateAction, StateAction stateAction) {
        for (PrismStateActionAssignment prismStateActionAssignment : prismStateAction.getAssignments()) {
            Role role = roleService.getById(prismStateActionAssignment.getRole());
            StateActionAssignment assignment = new StateActionAssignment().withStateAction(stateAction).withRole(role)
                    .withPartnerMode(prismStateActionAssignment.getPartnerMode()).withActionEnhancement(prismStateActionAssignment.getActionEnhancement());
            entityService.save(assignment);
            stateAction.getStateActionAssignments().add(assignment);
        }
    }

    private void initializeStateActionNotifications(PrismStateAction prismStateAction, StateAction stateAction) {
        for (PrismStateActionNotification prismStateActionNotification : prismStateAction.getNotifications()) {
            Role role = roleService.getById(prismStateActionNotification.getRole());
            NotificationDefinition template = notificationService.getById(prismStateActionNotification.getNotification());
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
            List<? extends WorkflowConfigurationDTO> configurationDTO) {
        if (configurationDTO.size() > 0) {
            customizationService.createConfigurationGroup(configurationType, system, prismScope,
                    prismScope.ordinal() > DEPARTMENT.ordinal() ? getSystemOpportunityType() : null, configurationDTO);
        }
    }

    private <T extends UniqueEntity> void verifyDefinition(Class<T> workflowResourceClass) throws WorkflowConfigurationException {
        try {
            List<T> entities = entityService.list(workflowResourceClass);
            for (T entity : entities) {
                Object id = PrismReflectionUtils.getProperty(entity, "id");
                if (PrismLocalizableDefinition.class.isAssignableFrom(id.getClass())) {
                    ((PrismLocalizableDefinition) id).getDisplayProperty();
                }
            }
        } catch (Exception e) {
            throw new WorkflowConfigurationException("incomplete " + workflowResourceClass.getSimpleName() + " definition", e);
        }
    }

}
