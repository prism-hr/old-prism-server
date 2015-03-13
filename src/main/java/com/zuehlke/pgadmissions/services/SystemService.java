package com.zuehlke.pgadmissions.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PrismReminderDefinition;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.*;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SocialMetadataDTO;
import com.zuehlke.pgadmissions.exceptions.*;
import com.zuehlke.pgadmissions.rest.dto.*;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO.DisplayPropertyConfigurationValueDTO;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO.StateDurationConfigurationValueDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.*;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_INITIALIZED_SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DESCRIPTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramType.getSystemProgramType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.SYSTEM_RUNNING;

@Service
public class SystemService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemService.class);

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

	@Transactional(timeout = 600)
	public void initializeSystem() throws WorkflowConfigurationException, DeduplicationException, CustomizationException, InstantiationException,
	        IllegalAccessException, BeansException, WorkflowEngineException, IOException, IntegrationException {
		LOGGER.info("Initializing scope definitions");
		verifyBackwardCompatibility(Scope.class);
		initializeScopes();

		LOGGER.info("Initializing role definitions");
		verifyBackwardCompatibility(Role.class);
		initializeRoles();

		LOGGER.info("Initializing action custom question definitions");
		verifyBackwardCompatibility(ActionCustomQuestionDefinition.class);
		initializeActionCustomQuestionDefinitions();

		LOGGER.info("Initializing action definitions");
		verifyBackwardCompatibility(Action.class);
		initializeActions();

		LOGGER.info("Initializing state group definitions");
		verifyBackwardCompatibility(StateGroup.class);
		initializeStateGroups();

		LOGGER.info("Initializing state definitions");
		verifyBackwardCompatibility(State.class);
		initializeStates();

		LOGGER.info("Initializing state duration definitions");
		verifyBackwardCompatibility(StateDurationDefinition.class);
		initializeStateDurationDefinitions();

		LOGGER.info("Initializing display property definitions");
		verifyBackwardCompatibility(DisplayPropertyDefinition.class);
		initializeDisplayPropertyDefinitions();

		LOGGER.info("Initializing workflow property definitions");
		verifyBackwardCompatibility(WorkflowPropertyDefinition.class);
		initializeWorkflowPropertyDefinitions();

		LOGGER.info("Initializing notification definitions");
		verifyBackwardCompatibility(NotificationDefinition.class);
		initializeNotificationDefinitions();

		LOGGER.info("Initializing state action definitions");
		initializeStateActions();

		LOGGER.info("Initializing system object");
		System system = initializeSystemResource();

		LOGGER.info("Initializing state duration configurations");
		initializeStateDurationConfigurations(system);

		LOGGER.info("Initializing display property configurations");
		initializeDisplayPropertyConfigurations(system);

		LOGGER.info("Initializing workflow property configurations");
		initializeWorkflowPropertyConfigurations(system);

		LOGGER.info("Initializing notification configurations");
		initializeNotificationConfigurations(system);

		LOGGER.info("Initializing system user");
		initializeSystemUser(system);

		entityService.flush();
		entityService.clear();
	}

	@Transactional
	public void setLastDataImportDate(LocalDate baseline) {
		getSystem().setLastDataImportDate(baseline);
	}

	@Transactional
	public SocialMetadataDTO getSocialMetadata() {
		System system = getSystem();
		PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(system);
		return new SocialMetadataDTO().withAuthor(system.getUser().getFullName()).withTitle(system.getTitle())
		        .withDescription(loader.load(SYSTEM_DESCRIPTION))
		        .withThumbnailUrl(resourceService.getSocialThumbnailUrl(system)).withResourceUrl(resourceService.getSocialResourceUrl(system))
		        .withLocale(resourceService.getOperativeLocale(system).toString());
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

	private void initializeScopes() throws DeduplicationException {
		for (PrismScope prismScope : PrismScope.values()) {
			entityService.createOrUpdate(new Scope().withId(prismScope).withShortCode(prismScope.getShortCode()).withOrdinal(prismScope.ordinal()));
		}
	}

	private void initializeRoles() throws DeduplicationException {
		for (PrismRole prismRole : PrismRole.values()) {
			Scope scope = scopeService.getById(prismRole.getScope());
			entityService.createOrUpdate(new Role().withId(prismRole).withRoleCategory(prismRole.getRoleCategory()).withScopeCreator(prismRole.isScopeOwner())
			        .withScope(scope));
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
			Scope creationScope = scopeService.getById(prismAction.getCreationScope());
			ActionCustomQuestionDefinition actionCustomQuestionDefinition = actionService
			        .getCustomQuestionDefinitionById(prismAction.getActionCustomQuestion());
			Action transientAction = new Action().withId(prismAction).withActionType(prismAction.getActionType())
			        .withActionCategory(prismAction.getActionCategory()).withRatingAction(prismAction.isRatingAction())
			        .withTransitionAction(prismAction.isTransitionAction()).withDeclinableAction(prismAction.isDeclinableAction())
			        .withVisibleAction(prismAction.isVisibleAction()).withEmphasizedAction(prismAction.isEmphasizedAction())
			        .withConcludeParentAction(prismAction.isConcludeParentAction()).withActionCustomQuestionDefinition(actionCustomQuestionDefinition)
			        .withScope(scope).withCreationScope(creationScope);
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

	private void initializeStateGroups() throws DeduplicationException {
		for (PrismStateGroup prismStateGroup : PrismStateGroup.values()) {
			Scope scope = scopeService.getById(prismStateGroup.getScope());
			StateGroup transientStateGroup = new StateGroup().withId(prismStateGroup).withSequenceOrder(prismStateGroup.getSequenceOrder())
			        .withRepeatable(prismStateGroup.isRepeatable()).withScope(scope);
			entityService.createOrUpdate(transientStateGroup);
		}
	}

	private void initializeStates() throws DeduplicationException {
		for (PrismState prismState : PrismState.values()) {
			StateDurationDefinition stateDurationDefinition = stateService.getStateDurationDefinitionById(prismState.getDefaultDuration());
			Scope scope = entityService.getByProperty(Scope.class, "id", prismState.getScope());
			StateGroup stateGroup = entityService.getByProperty(StateGroup.class, "id", prismState.getStateGroup());
			State transientState = new State().withId(prismState).withStateGroup(stateGroup).withStateDurationDefinition(stateDurationDefinition)
			        .withStateDurationEvaluation(prismState.getStateDurationEvaluation()).withParallelizable(prismState.isParallelizable())
			        .withHidden(prismState.isHidden()).withScope(scope);
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
			Scope scope = scopeService.getById(prismDisplayPropertyDefinition.getDisplayCategory().getScope());
			entityService.createOrUpdate(new DisplayPropertyDefinition().withId(prismDisplayPropertyDefinition)
			        .withCategory(prismDisplayPropertyDefinition.getDisplayCategory()).withScope(scope));
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
		User systemUser = userService.getOrCreateUser(systemUserFirstName, systemUserLastName, systemUserEmail, PrismLocale.getSystemLocale());
		DateTime baseline = new DateTime();

		if (system == null) {
			State systemRunning = stateService.getById(SYSTEM_RUNNING);
			system = new System().withId(systemId).withTitle(systemName).withLocale(PrismLocale.getSystemLocale()).withHelpdesk(systemHelpdesk)
			        .withUser(systemUser).withState(systemRunning).withCipherSalt(EncryptionUtils.getUUID()).withCreatedTimestamp(baseline)
			        .withUpdatedTimestamp(baseline);
			entityService.save(system);

			ResourceState systemState = new ResourceState().withResource(system).withState(systemRunning).withPrimaryState(true);
			entityService.save(systemState);
			system.getResourceStates().add(systemState);
		} else {
			system.setId(systemId);
			system.setTitle(systemName);
			system.setLocale(PrismLocale.getSystemLocale());
			system.setHelpdesk(systemHelpdesk);
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
			DisplayPropertyConfigurationDTO configurationDTO = new DisplayPropertyConfigurationDTO();
			for (PrismDisplayPropertyDefinition prismDisplayPropertyDefinition : PrismDisplayPropertyDefinition.values()) {
				if (prismScope == prismDisplayPropertyDefinition.getDisplayCategory().getScope()) {
					configurationDTO.add(new DisplayPropertyConfigurationValueDTO().withDefinitionId(prismDisplayPropertyDefinition).withValue(
					        prismDisplayPropertyDefinition.getDefaultValue()));
				}
			}
			persistConfigurations(DISPLAY_PROPERTY, system, prismScope, configurationDTO);
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

			PrismProgramType programType = prismNotificationDefinition.getScope().ordinal() > INSTITUTION.ordinal() ? PrismProgramType
			        .getSystemProgramType() : null;

			NotificationConfigurationDTO configurationDTO = new NotificationConfigurationDTO().withId(prismNotificationDefinition).withSubject(subject)
			        .withContent(content).withReminderInterval(prismNotificationDefinition.getDefaultReminderDuration());
			customizationService.createOrUpdateConfiguration(NOTIFICATION, system, getSystemLocale(), programType, configurationDTO);
		}
	}

	private void initializeStateActions() throws DeduplicationException, WorkflowConfigurationException {
		stateService.deleteStateActions();

		for (State state : stateService.getStates()) {
			for (PrismStateAction prismStateAction : PrismState.getStateActions(state.getId())) {
				Action action = actionService.getById(prismStateAction.getAction());
				NotificationDefinition template = notificationService.getById(prismStateAction.getNotificationTemplate());
				StateAction stateAction = new StateAction().withState(state).withAction(action).withRaisesUrgentFlag(prismStateAction.isRaisesUrgentFlag())
				        .withDefaultAction(prismStateAction.isDefaultAction()).withActionEnhancement(prismStateAction.getActionEnhancement())
				        .withNotificationDefinition(template);
				entityService.save(stateAction);
				state.getStateActions().add(stateAction);

				initializeStateActionAssignments(prismStateAction, stateAction);
				initializeStateActionNotifications(prismStateAction, stateAction);
				initializeStateTransitions(prismStateAction, stateAction);
			}
		}

		stateService.deleteObsoleteStateDurations();
		notificationService.deleteObsoleteNotificationConfigurations();
		roleService.deleteObseleteUserRoles();
	}

	private void initializeStateActionAssignments(PrismStateAction prismStateAction, StateAction stateAction) {
		for (PrismStateActionAssignment prismAssignment : prismStateAction.getAssignments()) {
			Role role = roleService.getById(prismAssignment.getRole());
			Action delegateAction = actionService.getById(prismAssignment.getDelegatedAction());
			StateActionAssignment assignment = new StateActionAssignment().withStateAction(stateAction).withRole(role)
			        .withActionEnhancement(prismAssignment.getActionEnhancement()).withDelegatedAction(delegateAction);
			entityService.save(assignment);
			stateAction.getStateActionAssignments().add(assignment);
		}
	}

	private void initializeStateActionNotifications(PrismStateAction prismStateAction, StateAction stateAction) {
		for (PrismStateActionNotification prismNotification : prismStateAction.getNotifications()) {
			Role role = roleService.getById(prismNotification.getRole());
			NotificationDefinition template = notificationService.getById(prismNotification.getDefinition());
			StateActionNotification notification = new StateActionNotification().withStateAction(stateAction).withRole(role)
			        .withNotificationDefinition(template);
			entityService.save(notification);
			stateAction.getStateActionNotifications().add(notification);
		}
	}

	private void initializeStateTransitions(PrismStateAction prismStateAction, StateAction stateAction) {
		for (PrismStateTransition prismStateTransition : prismStateAction.getTransitions()) {
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

	private void initializeSystemUser(System system) throws DeduplicationException, InstantiationException, IllegalAccessException, BeansException,
	        WorkflowEngineException, IOException, IntegrationException {
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

	private void persistConfigurations(PrismConfiguration configurationType, System system, PrismScope prismScope,
	        List<? extends WorkflowConfigurationDTO> configurationDTO) throws CustomizationException, DeduplicationException, InstantiationException,
	        IllegalAccessException {
		if (configurationDTO.size() > 0) {
			customizationService.createConfigurationGroup(configurationType, system, prismScope, getSystemLocale(),
			        prismScope.ordinal() > INSTITUTION.ordinal() ? getSystemProgramType() : null, configurationDTO);
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
