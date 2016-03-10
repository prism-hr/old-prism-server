package uk.co.alumeni.prism.services;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static uk.co.alumeni.prism.dao.WorkflowDAO.advertScopes;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_INITIALIZED_SYSTEM;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityType.getSystemOpportunityType;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_STARTUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration.DISPLAY_PROPERTY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration.NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration.STATE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.SYSTEM_RUNNING;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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

import uk.co.alumeni.prism.dao.SystemDAO;
import uk.co.alumeni.prism.domain.AgeRange;
import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.PrismAgeRange;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismDomicile;
import uk.co.alumeni.prism.domain.definitions.PrismLocalizableDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionRedaction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateActionAssignment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateActionRecipient;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTermination;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionNotification;
import uk.co.alumeni.prism.domain.display.DisplayPropertyConfiguration;
import uk.co.alumeni.prism.domain.display.DisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.resource.ResourceState;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.ActionRedaction;
import uk.co.alumeni.prism.domain.workflow.NotificationDefinition;
import uk.co.alumeni.prism.domain.workflow.OpportunityType;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.domain.workflow.RoleTransition;
import uk.co.alumeni.prism.domain.workflow.Scope;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateAction;
import uk.co.alumeni.prism.domain.workflow.StateActionAssignment;
import uk.co.alumeni.prism.domain.workflow.StateActionRecipient;
import uk.co.alumeni.prism.domain.workflow.StateDurationDefinition;
import uk.co.alumeni.prism.domain.workflow.StateGroup;
import uk.co.alumeni.prism.domain.workflow.StateTermination;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.domain.workflow.StateTransitionEvaluation;
import uk.co.alumeni.prism.domain.workflow.StateTransitionNotification;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.exceptions.DeduplicationException;
import uk.co.alumeni.prism.exceptions.IntegrationException;
import uk.co.alumeni.prism.exceptions.WorkflowConfigurationException;
import uk.co.alumeni.prism.rest.dto.DisplayPropertyConfigurationDTO;
import uk.co.alumeni.prism.rest.dto.NotificationConfigurationDTO;
import uk.co.alumeni.prism.rest.dto.StateDurationConfigurationDTO;
import uk.co.alumeni.prism.rest.dto.StateDurationConfigurationDTO.StateDurationConfigurationValueDTO;
import uk.co.alumeni.prism.rest.dto.WorkflowConfigurationDTO;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;
import uk.co.alumeni.prism.utils.PrismEncryptionUtils;
import uk.co.alumeni.prism.utils.PrismFileUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;

@Service
public class SystemService {

    private static final Logger logger = LoggerFactory.getLogger(SystemService.class);

    private PropertyLoader propertyLoader;

    @Value("${context.environment}")
    private String environment;

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
    private SystemDAO systemDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private AddressService addressService;

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
    private ApplicationContext applicationContext;

    @Transactional
    public System getSystem() {
        return entityService.getByProperty(System.class, "name", systemName);
    }

    @Transactional(timeout = 600)
    public void dropWorkflow() {
        if (asList("prod", "uat").contains(environment)) {
            throw new Error("You tried to clear the " + environment + " database, destroying all data. Did you really mean to do that?");
        } else if (environment.equals("dev")) {
            logger.info("Destroying workflow properties");
            systemDAO.clearSchema();
        }
    }

    @Transactional(timeout = 600)
    public void initializeWorkflow() throws Exception {
        logger.info("Initializing opportunity type definitions");
        verifyDefinition(OpportunityType.class);
        initializeOpportunityTypes();

        logger.info("Initializing age range definitions");
        verifyDefinition(AgeRange.class);
        initializeAgeRanges();

        logger.info("Initializing domicile definitions");
        verifyDefinition(Domicile.class);
        initializeDomiciles();

        logger.info("Initializing scope definitions");
        verifyDefinition(Scope.class);
        initializeScopes();

        logger.info("Initializing role definitions");
        verifyDefinition(Role.class);
        initializeRoles();

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

        logger.info("Initializing notification definitions");
        verifyDefinition(NotificationDefinition.class);
        initializeNotificationDefinitions();

        logger.info("Initializing state action definitions");
        initializeStateActions();

        logger.info("Initializing system object");
        System system = initializeSystemResource();

        logger.info("Initializing state duration configurations");
        initializeStateDurationConfigurations(system);

        logger.info("Initializing notification configurations");
        initializeNotificationConfigurations(system);

        entityService.flush();
        entityService.clear();
    }

    @Transactional(timeout = 600)
    public void dropDisplayProperties() {
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

    public void initializeSectionCompleteness() throws Exception {
        logger.info("Initializing advert section completeness");
        for (PrismScope resourceScope : advertScopes) {
            for (Integer resourceId : resourceService.getResourceIds(resourceScope)) {
                resourceService.setResourceAdvertIncompleteSection(resourceScope, resourceId);
            }

        }
    }

    public void initializeAddressCompleteness() throws Exception {
        logger.info("Initializing address location completeness");
        for (Integer addressId : addressService.getAddressesWithNoLocationParts()) {
            addressService.geocodeAddressAsEstablishment(addressId);
        }
    }

    public void initializePropertyLoader() {
        logger.info("Initializing default display property loader");
        this.propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeEager(getSystem());
    }

    @Transactional
    public void initializeSystemUser() throws Exception {
        System system = getSystem();
        User user = system.getUser();
        if (user.getUserAccount() == null) {
            Action action = actionService.getById(SYSTEM_STARTUP);
            String content = applicationContext.getBean(PropertyLoader.class).localizeLazy(system).loadLazy(SYSTEM_COMMENT_INITIALIZED_SYSTEM);
            Comment comment = new Comment().withUser(user).withAction(action).withContent(content).withDeclinedResponse(false)
                    .withCreatedTimestamp(new DateTime()).addAssignedUser(user, roleService.getCreatorRole(system), CREATE);
            ActionOutcomeDTO outcome = actionService.executeAction(system, action, comment);
            notificationService.sendCompleteRegistrationRequest(user, outcome);
        }
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

    public PropertyLoader getPropertyLoader() {
        return propertyLoader;
    }

    private void initializeOpportunityTypes() throws DeduplicationException {
        for (PrismOpportunityType prismOpportunityType : PrismOpportunityType.values()) {
            entityService.createOrUpdate(new OpportunityType().withId(prismOpportunityType)
                    .withOpportunityCategory(prismOpportunityType.getOpportunityCategory())
                    .withPublished(prismOpportunityType.isPublished()).withOrdinal(prismOpportunityType.ordinal()));
        }
    }

    private void initializeAgeRanges() throws DeduplicationException {
        for (PrismAgeRange prismAgeRange : PrismAgeRange.values()) {
            entityService.createOrUpdate(new AgeRange().withId(prismAgeRange).withLowerBound(prismAgeRange.getLowerBound())
                    .withUpperBound(prismAgeRange.getUpperBound())
                    .withOrdinal(prismAgeRange.ordinal()));
        }
    }

    private void initializeDomiciles() throws DeduplicationException {
        for (PrismDomicile prismDomicile : PrismDomicile.values()) {
            entityService.createOrUpdate(new Domicile().withId(prismDomicile).withCurrency(prismDomicile.getCurrency()).withOrdinal(prismDomicile.ordinal()));
        }
    }

    private void initializeScopes() throws DeduplicationException {
        for (PrismScope prismScope : PrismScope.values()) {
            entityService.createOrUpdate(new Scope().withId(prismScope).withScopeCategory(prismScope.getScopeCategory())
                    .withShortCode(prismScope.getShortCode())
                    .withDefaultShared(prismScope.isDefaultShared()).withOrdinal(prismScope.ordinal()));
        }
    }

    private void initializeRoles() throws DeduplicationException {
        for (PrismRole prismRole : PrismRole.values()) {
            Scope scope = scopeService.getById(prismRole.getScope());
            entityService.createOrUpdate(new Role().withId(prismRole).withRoleCategory(prismRole.getRoleCategory()).withVerified(false)
                    .withDirectlyAssignable(prismRole.isDirectlyAssignable()).withScope(scope));
        }
    }

    private void initializeActions() throws DeduplicationException {
        entityService.deleteAll(ActionRedaction.class);

        for (PrismAction prismAction : PrismAction.values()) {
            Scope scope = scopeService.getById(prismAction.getScope());
            Action transientAction = new Action().withId(prismAction).withSystemInvocationOnly(prismAction.isSystemInvocationOnly())
                    .withActionCategory(prismAction.getActionCategory()).withRatingAction(prismAction.isRatingAction())
                    .withTransitionAction(prismAction.isTransitionAction())
                    .withDeclinableAction(prismAction.isDeclinableAction()).withVisibleAction(prismAction.isVisibleAction())
                    .withReplicableUserAssignmentAction(prismAction.isReplicableUserAssignmentAction()).withPartnershipState(prismAction.getPartnershipState())
                    .withPartnershipTransitionState(prismAction.getPartnershipTransitionState()).withScope(scope);
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
        PrismScope lastPrismScope = null;
        for (PrismStateGroup prismStateGroup : PrismStateGroup.values()) {
            PrismScope prismScope = prismStateGroup.getScope();
            if (!Objects.equal(prismScope, lastPrismScope)) {
                ordinal = 0;
                lastPrismScope = prismScope;
            }
            Scope scope = scopeService.getById(prismStateGroup.getScope());
            StateGroup transientStateGroup = new StateGroup().withId(prismStateGroup).withOrdinal(ordinal).withScope(scope);
            entityService.createOrUpdate(transientStateGroup);
            ordinal++;
        }
    }

    private void initializeStates() throws DeduplicationException {
        int ordinal = 0;
        StateGroup lastStateGroup = null;
        for (PrismState prismState : PrismState.values()) {
            PrismStateGroup prismStateGroup = prismState.getStateGroup();
            Scope scope = scopeService.getById(prismStateGroup.getScope());

            StateGroup stateGroup = stateService.getStateGroupById(prismStateGroup);
            if (!Objects.equal(stateGroup, lastStateGroup)) {
                ordinal = 0;
                lastStateGroup = stateGroup;
            }

            State transientState = new State().withId(prismState).withOrdinal(ordinal).withStateGroup(stateGroup)
                    .withStateDurationDefinition(stateService.getStateDurationDefinitionById(prismState.getDefaultDuration()))
                    .withStateDurationEvaluation(prismState.getStateDurationEvaluation()).withScope(scope);
            entityService.createOrUpdate(transientState);
            ordinal++;
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
    }

    private void initializeDisplayPropertyDefinitions() throws DeduplicationException {
        for (PrismDisplayPropertyDefinition prismDisplayPropertyDefinition : PrismDisplayPropertyDefinition.values()) {
            Scope scope = scopeService.getById(prismDisplayPropertyDefinition.getCategory().getScope());
            entityService.createOrUpdate(new DisplayPropertyDefinition().withId(prismDisplayPropertyDefinition)
                    .withCategory(prismDisplayPropertyDefinition.getCategory()).withScope(scope));
        }
    }

    private System initializeSystemResource() throws DeduplicationException {
        System system = getSystem();
        User systemUser = userService.getOrCreateUser(systemUserFirstName, systemUserLastName, systemUserEmail);
        DateTime baseline = new DateTime();

        if (system == null) {
            State systemRunning = stateService.getById(SYSTEM_RUNNING);
            system = new System().withId(systemId).withName(systemName).withUser(systemUser).withShared(SYSTEM.isDefaultShared()).withState(systemRunning)
                    .withCipherSalt(PrismEncryptionUtils.getUUID()).withCreatedTimestamp(baseline).withUpdatedTimestamp(baseline);
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
                            .withValue(prismDisplayPropertyDefinition.getDefaultValue());
                    customizationService.createOrUpdateConfiguration(DISPLAY_PROPERTY, system,
                            prismScope.ordinal() > DEPARTMENT.ordinal() ? getSystemOpportunityType() : null, configurationDTO);
                }
            }
        }
    }

    private void initializeNotificationConfigurations(System system) {
        for (PrismNotificationDefinition prismNotificationDefinition : PrismNotificationDefinition.values()) {
            String subject = PrismFileUtils.getContent(defaultEmailSubjectDirectory + prismNotificationDefinition.getInitialTemplateSubject());
            String content = PrismFileUtils.getContent(defaultEmailContentDirectory + prismNotificationDefinition.getInitialTemplateContent());

            PrismOpportunityType opportunityType = prismNotificationDefinition.getScope().ordinal() > DEPARTMENT.ordinal() ? PrismOpportunityType
                    .getSystemOpportunityType() : null;

            NotificationConfigurationDTO configurationDTO = new NotificationConfigurationDTO().withId(prismNotificationDefinition).withSubject(subject)
                    .withContent(content);
            customizationService.createOrUpdateConfiguration(NOTIFICATION, system, opportunityType, configurationDTO);
        }
    }

    private void initializeStateActions() throws DeduplicationException, WorkflowConfigurationException {
        stateService.deleteStateActions();

        for (State state : stateService.getStates()) {
            for (PrismStateAction prismStateAction : PrismState.getStateActions(state.getId())) {
                Action action = actionService.getById(prismStateAction.getAction());
                initializeStateAction(state, action, prismStateAction);
            }
        }

        actionService.setCreationActions();
        actionService.setFallbackActions();
        actionService.setStateGroupTransitionActions();

        stateService.setRepeatableStateGroups();
        stateService.setHiddenStates();
        stateService.setParallelizableStates();

        roleService.setCreatorRoles();
        roleService.setVerifiedRoles();

        stateService.deleteObsoleteStateDurations();
        notificationService.deleteObsoleteNotificationConfigurations();
        roleService.deleteObsoleteUserRoles();
    }

    private void initializeStateAction(State state, Action action, PrismStateAction prismStateAction) {
        StateAction stateAction = new StateAction().withState(state).withAction(action).withRaisesUrgentFlag(prismStateAction.getRaisesUrgentFlag())
                .withReplicableSequenceStart(prismStateAction.getReplicableSequenceStart()).withActionEnhancement(prismStateAction.getActionEnhancement());

        PrismNotificationDefinition prismNotificationDefiniton = prismStateAction.getNotificationDefinition();
        if (prismNotificationDefiniton != null) {
            stateAction.setNotificationDefinition(notificationService.getById(prismNotificationDefiniton));
        }

        entityService.save(stateAction);
        state.getStateActions().add(stateAction);

        initializeStateActionAssignments(prismStateAction, stateAction);
        initializeStateTransitions(prismStateAction, stateAction);
    }

    private void initializeStateActionAssignments(PrismStateAction prismStateAction, StateAction stateAction) {
        for (PrismStateActionAssignment prismStateActionAssignment : prismStateAction.getStateActionAssignments()) {
            Role role = roleService.getById(prismStateActionAssignment.getRole());
            StateActionAssignment stateActionAssignment = new StateActionAssignment().withStateAction(stateAction).withRole(role)
                    .withExternalMode(prismStateActionAssignment.getExternalMode()).withActionEnhancement(prismStateActionAssignment.getActionEnhancement());
            entityService.save(stateActionAssignment);
            stateAction.getStateActionAssignments().add(stateActionAssignment);
            initializeStateActionRecipients(prismStateActionAssignment, stateActionAssignment);
        }
    }

    private void initializeStateActionRecipients(PrismStateActionAssignment prismStateActionAssignment, StateActionAssignment stateActionAssignment) {
        for (PrismStateActionRecipient prismStateActionRecipient : prismStateActionAssignment.getStateActionRecipients()) {
            Role recipientRole = roleService.getById(prismStateActionRecipient.getRole());
            StateActionRecipient stateActionRecipient = new StateActionRecipient().withStateActionAssignment(stateActionAssignment)
                    .withRole(recipientRole).withExternalMode(prismStateActionRecipient.getExternalMode());
            entityService.save(stateActionRecipient);
            stateActionAssignment.addStateActionRecipient(stateActionRecipient);
        }
    }

    private void initializeStateTransitions(PrismStateAction prismStateAction, StateAction stateAction) {
        Set<PrismStateTransition> stateTransitions = prismStateAction.getStateTransitions();
        if (isEmpty(stateTransitions)) {
            stateTransitions.add(new PrismStateTransition().withTransitionState(stateAction.getState().getId()) //
                    .withTransitionAction(stateAction.getAction().getId()));
        }

        for (PrismStateTransition prismStateTransition : stateTransitions) {
            State transitionState = stateService.getById(prismStateTransition.getTransitionState());
            Action transitionAction = actionService.getById(prismStateTransition.getTransitionAction());

            StateTransitionEvaluation stateTransitionEvaluation = null;
            PrismStateTransitionEvaluation prismStateTransitionEvaluation = prismStateTransition.getStateTransitionEvaluation();
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
                    .withTransitionAction(transitionAction)
                    .withReplicableSequenceClose(prismStateTransition.getReplicableSequenceClose())
                    .withReplicableSequenceFilterTheme(prismStateTransition.getReplicableSequenceFilterTheme())
                    .withReplicableSequenceFilterSecondaryTheme(prismStateTransition.getReplicableSequenceFilterSecondaryTheme())
                    .withReplicableSequenceFilterLocation(prismStateTransition.getReplicableSequenceFilterLocation())
                    .withReplicableSequenceFilterSecondaryLocation(prismStateTransition.getReplicableSequenceFilterSecondaryLocation())
                    .withStateTransitionEvaluation(stateTransitionEvaluation);
            entityService.save(stateTransition);
            stateAction.getStateTransitions().add(stateTransition);

            initializeStateTransitionNotifications(prismStateTransition, stateTransition);
            initializeRoleTransitions(prismStateTransition, stateTransition);
            initializeStateTerminations(prismStateTransition, stateTransition);
            initializePropagatedActions(prismStateTransition, stateTransition);
        }
    }

    private void initializeStateTransitionNotifications(PrismStateTransition prismStateTransition, StateTransition stateTransition) {
        for (PrismStateTransitionNotification prismStateActionNotification : prismStateTransition.getStateTransitionNotifications()) {
            Role role = roleService.getById(prismStateActionNotification.getRole());
            NotificationDefinition notificationDefinition = notificationService.getById(prismStateActionNotification.getNotificationdDefinition());
            StateTransitionNotification stateTransitionNotification = new StateTransitionNotification().withStateAction(stateTransition).withRole(role)
                    .withNotificationDefinition(notificationDefinition);
            entityService.save(stateTransitionNotification);
            stateTransition.getStateTransitionNotifications().add(stateTransitionNotification);
        }
    }

    private void initializeRoleTransitions(PrismStateTransition prismStateTransition, StateTransition stateTransition) {
        for (PrismRoleTransition prismRoleTransition : prismStateTransition.getRoleTransitions()) {
            Role role = roleService.getById(prismRoleTransition.getRole());
            Role transitionRole = roleService.getById(prismRoleTransition.getTransitionRole());
            RoleTransition roleTransition = new RoleTransition().withStateTransition(stateTransition).withRole(role)
                    .withRoleTransitionType(prismRoleTransition.getTransitionType()).withTransitionRole(transitionRole)
                    .withRestrictToActionOwner(prismRoleTransition.getRestrictToActionOwner()).withMinimumPermitted(prismRoleTransition.getMinimumPermitted())
                    .withMaximumPermitted(prismRoleTransition.getMaximumPermitted());
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

    private <T extends UniqueEntity> void verifyDefinition(Class<T> definitionClass) throws WorkflowConfigurationException {
        try {
            entityService.getAll(definitionClass).forEach(definition -> {
                Object id = getProperty(definition, "id");
                if (PrismLocalizableDefinition.class.isAssignableFrom(id.getClass())) {
                    ((PrismLocalizableDefinition) id).getDisplayProperty();
                }
            });
        } catch (Exception e) {
            throw new WorkflowConfigurationException("Incomplete " + definitionClass.getSimpleName() + " definition", e);
        }
    }

}
