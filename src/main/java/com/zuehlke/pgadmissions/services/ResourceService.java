package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentStateDefinition;
import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.ResourceListFilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.resource.ResourceStateDefinition;
import com.zuehlke.pgadmissions.domain.resource.ResourceStateTransitionSummary;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SocialMetadataDTO;
import com.zuehlke.pgadmissions.dto.UserAdministratorResourceDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.builders.ResourceListConstraintBuilder;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismConstants;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Service
@Transactional
@SuppressWarnings("unchecked")
public class ResourceService {

	@Value("${application.url}")
	private String applicationUrl;

	@Value("${application.api.url}")
	private String applicationApiUrl;

	@Inject
	private ResourceDAO resourceDAO;

	@Inject
	private ActionService actionService;

	@Inject
	private ApplicationService applicationService;

	@Inject
	private ApplicationSummaryService applicationSummaryService;

	@Inject
	private ProjectService projectService;

	@Inject
	private ProgramService programService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private SystemService systemService;

	@Inject
	private EntityService entityService;

	@Inject
	private NotificationService notificationService;

	@Inject
	private ResourceListFilterService resourceListFilterService;

	@Inject
	private RoleService roleService;

	@Inject
	private ScopeService scopeService;

	@Inject
	private StateService stateService;

	@Inject
	private UserService userService;

	@Inject
	private CustomizationService customizationService;

	@Inject
	private ApplicationContext applicationContext;

	public <T extends Resource> T getById(Class<T> resourceClass, Integer id) {
		return entityService.getById(resourceClass, id);
	}

	public Integer getResourceId(Resource resource) {
		return resource == null ? null : resource.getId();
	}

	public ActionOutcomeDTO create(User user, Action action, Object resourceDTO, String referrer, Integer workflowPropertyConfigurationVersion)
	        throws Exception {
		Resource resource;
		PrismScope resourceScope = action.getCreationScope().getId();

		User resourceUser = user.getParentUser();
		switch (resourceScope) {
		case INSTITUTION:
			resource = institutionService.create(resourceUser, (InstitutionDTO) resourceDTO);
			break;
		case PROGRAM:
			resource = programService.create(resourceUser, (ProgramDTO) resourceDTO);
			break;
		case PROJECT:
			resource = projectService.create(resourceUser, (ProjectDTO) resourceDTO);
			break;
		case APPLICATION:
			resource = applicationService.create(resourceUser, (ApplicationDTO) resourceDTO);
			break;
		default:
			throw new Error("Attempted to create a resource of invalid type");
		}

		resource.setReferrer(referrer);
		resource.setWorkflowPropertyConfigurationVersion(workflowPropertyConfigurationVersion);

		resourceUser.setLatestCreationScope(scopeService.getById(resourceScope));
		Comment comment = new Comment().withUser(resourceUser).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
		        .addAssignedUser(resourceUser, roleService.getCreatorRole(resource), CREATE);

		return actionService.executeUserAction(resource, action, comment);
	}

	public void persistResource(Resource resource, Comment comment) throws WorkflowEngineException, BeansException, IOException, IntegrationException {
		if (comment.isCreateComment()) {
			DateTime baseline = new DateTime();
			resource.setCreatedTimestamp(baseline);
			resource.setUpdatedTimestamp(baseline);

			if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
				ReflectionUtils.setProperty(resource, "updatedTimestampSitemap", baseline);
			}

			switch (resource.getResourceScope()) {
			case INSTITUTION:
				institutionService.save((Institution) resource);
				break;
			case PROGRAM:
				programService.save((Program) resource);
				break;
			case PROJECT:
				projectService.save((Project) resource);
				break;
			case APPLICATION:
				applicationService.save((Application) resource);
				break;
			default:
				throw new WorkflowEngineException("Attempted to create resource of invalid type");
			}

			resource.setCode(generateResourceCode(resource));
			entityService.save(resource);
		}
		entityService.flush();
	}

	public ActionOutcomeDTO executeAction(Integer resourceId, CommentDTO commentDTO) throws DeduplicationException, InstantiationException,
	        IllegalAccessException, BeansException, WorkflowEngineException, IOException, IntegrationException {
		switch (commentDTO.getAction().getScope()) {
		case APPLICATION:
			return applicationService.executeAction(resourceId, commentDTO);
		case PROJECT:
			return projectService.executeAction(resourceId, commentDTO);
		case PROGRAM:
			return programService.executeAction(resourceId, commentDTO);
		case INSTITUTION:
			return institutionService.executeAction(resourceId, commentDTO);
		default:
			throw new Error();
		}
	}

	public void preProcessResource(Resource resource, Comment comment) {
		switch (resource.getResourceScope()) {
		case APPLICATION:
			applicationService.preProcessApplication((Application) resource, comment);
			break;
		default:
			break;
		}
	}

	public void recordStateTransition(Resource resource, Comment comment, State state, State transitionState) throws DeduplicationException,
	        InstantiationException, IllegalAccessException {
		resource.setPreviousState(state);
		resource.setState(transitionState);

		deleteResourceStates(resource.getResourcePreviousStates());
		deleteResourceStates(resource.getResourceStates());
		entityService.flush();

		insertResourceStates(resource, resource.getResourcePreviousStates(), comment.getCommentStates(), ResourcePreviousState.class);
		insertResourceStates(resource, resource.getResourceStates(), comment.getCommentTransitionStates(), ResourceState.class);
		entityService.flush();
	}

	public void processResource(Resource resource, Comment comment) throws DeduplicationException {
		StateDurationDefinition stateDurationDefinition = resource.getState().getStateDurationDefinition();
		if (comment.isStateTransitionComment() || (stateDurationDefinition != null && BooleanUtils.isTrue(stateDurationDefinition.getEscalation()))) {
			LocalDate baselineCustom = null;
			LocalDate baseline = new LocalDate();

			PrismStateDurationEvaluation stateDurationEvaluation = resource.getState().getStateDurationEvaluation();
			if (stateDurationEvaluation != null) {
				baselineCustom = applicationContext.getBean(stateDurationEvaluation.getResolver()).resolve(resource, comment);
			}

			baseline = baselineCustom == null || baselineCustom.isBefore(baseline) ? baseline : baselineCustom;

			StateDurationConfiguration stateDurationConfiguration = stateDurationDefinition == null ? null : stateService.getStateDurationConfiguration(
			        resource, comment.getUser(), stateDurationDefinition);
			resource.setDueDate(baseline.plusDays(stateDurationConfiguration == null ? 0 : stateDurationConfiguration.getDuration()));

			entityService.flush();
		}
	}

	public void postProcessResource(Resource resource, Comment comment) throws DeduplicationException {
		DateTime baselineTime = new DateTime();

		if (comment.isUserComment() || resource.getSequenceIdentifier() == null) {
			resource.setUpdatedTimestamp(baselineTime);
			resource.setSequenceIdentifier(Long.toString(baselineTime.getMillis()) + String.format("%010d", resource.getId()));
		}

		switch (resource.getResourceScope()) {
		case PROGRAM:
			programService.postProcessProgram((Program) resource, comment);
			break;
		case PROJECT:
			projectService.postProcessProject((Project) resource, comment);
			break;
		case APPLICATION:
			applicationService.postProcessApplication((Application) resource, comment);
			break;
		default:
			break;
		}

		if (comment.isUserCreationComment()) {
			notificationService.resetNotifications(resource);
		}

		if (comment.isStateGroupTransitionComment() && comment.getAction().getCreationScope() == null) {
			createOrUpdateStateTransitionSummary(resource, baselineTime);
		}
	}

	public String generateResourceCode(Resource resource) {
		return "PRiSM-" + PrismScope.getByResourceClass(resource.getClass()).getShortCode() + "-" + String.format("%010d", resource.getId());
	}

	public void executeUpdate(Resource resource, PrismDisplayPropertyDefinition messageIndex, CommentAssignedUser... assignees) throws DeduplicationException,
	        InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException, IntegrationException {
		User user = userService.getCurrentUser();
		Action action = actionService.getViewEditAction(resource);

		Comment comment = new Comment().withUser(user).withAction(action)
		        .withContent(applicationContext.getBean(PropertyLoader.class).localize(resource).load(messageIndex)).withDeclinedResponse(false)
		        .withCreatedTimestamp(new DateTime());

		for (CommentAssignedUser assignee : assignees) {
			comment.addAssignedUser(assignee.getUser(), assignee.getRole(), assignee.getRoleTransitionType());
			entityService.evict(assignee);
		}

		actionService.executeUserAction(resource, action, comment);
	}

	public void deleteResourceState(Resource resource, State state) {
		resourceDAO.deleteResourceState(resource, state);
	}

	public void deleteSecondaryResourceState(Resource resource, State state) {
		resourceDAO.deleteSecondaryResourceState(resource, state);
	}

	public Resource getOperativeResource(Resource resource, Action action) {
		return Arrays.asList(PrismActionCategory.CREATE_RESOURCE, PrismActionCategory.IMPORT_RESOURCE).contains(action.getActionCategory()) ? resource
		        .getParentResource() : resource;
	}

	public <T extends Resource> List<Integer> getResourcesToEscalate(Class<T> resourceClass, PrismAction actionId, LocalDate baseline) {
		return resourceDAO.getResourcesToEscalate(resourceClass, actionId, baseline);
	}

	public <T extends Resource> List<Integer> getResourcesToPropagate(PrismScope propagatingResourceScope, Integer propagatingResourceId,
	        PrismScope propagatedResourceScope, PrismAction actionId) {
		return resourceDAO.getResourcesToPropagate(propagatingResourceScope, propagatingResourceId, propagatedResourceScope, actionId);
	}

	public <T extends Resource> List<Integer> getResourcesRequiringIndividualReminders(Class<T> resourceClass, LocalDate baseline) {
		return resourceDAO.getResourcesRequiringIndividualReminders(resourceClass, baseline);
	}

	public <T extends Resource> List<Integer> getResourcesRequiringSyndicatedReminders(Class<T> resourceClass, LocalDate baseline) {
		return resourceDAO.getResourcesRequiringSyndicatedReminders(resourceClass, baseline);
	}

	public <T extends Resource> List<Integer> getResourcesRequiringSyndicatedUpdates(Class<T> resourceClass, LocalDate baseline, DateTime rangeStart,
	        DateTime rangeClose) {
		return resourceDAO.getResourceRequiringSyndicatedUpdates(resourceClass, baseline, rangeStart, rangeClose);
	}

	public <T extends Resource> List<ResourceListRowDTO> getResourceList(PrismScope scopeId, ResourceListFilterDTO filter, String lastSequenceIdentifier)
	        throws DeduplicationException {
		User user = userService.getCurrentUser();
		List<PrismScope> parentScopeIds = scopeService.getParentScopesDescending(scopeId);
		filter = resourceListFilterService.saveOrGetByUserAndScope(user, scopeId, filter);

		int maxRecords = PrismConstants.LIST_PAGE_ROW_COUNT;
		Set<Integer> assignedResources = getAssignedResources(user, scopeId, parentScopeIds, filter, lastSequenceIdentifier, maxRecords);
		boolean hasRedactions = actionService.hasRedactions(scopeId, assignedResources, user);

		return assignedResources.isEmpty() ? new ArrayList<ResourceListRowDTO>() : resourceDAO.getResourceConsoleList(user, scopeId, parentScopeIds,
		        assignedResources, filter, lastSequenceIdentifier, maxRecords, hasRedactions);
	}

	public <T extends Resource> ResourceSummaryRepresentation getResourceSummary(Class<T> resourceClass, Integer resourceId) {
		ResourceParent resource = (ResourceParent) getById(resourceClass, resourceId);

		ResourceSummaryRepresentation summary = new ResourceSummaryRepresentation().withCreatedDate(resource.getCreatedTimestamp().toLocalDate())
		        .withApplicationCreatedCount(resource.getApplicationCreatedCount()).withApplicationSubmittedCount(resource.getApplicationSubmittedCount())
		        .withApplicationApprovedCount(resource.getApplicationApprovedCount()).withApplicationRejectedCount(resource.getApplicationRejectedCount())
		        .withApplicationWithdrawnCount(resource.getApplicationWithdrawnCount()).withApplicationRatingCount(resource.getApplicationRatingCount())
		        .withApplicationRatingOccurenceAverage(resource.getApplicationRatingCountAverageNonZero());

		if (resourceClass == Institution.class) {
			summary.setProgramCount(programService.getActiveProgramCount((Institution) resource));
			summary.setProjectCount(projectService.getActiveProjectCount(resource));
		} else if (resourceClass == Program.class) {
			summary.setProjectCount(projectService.getActiveProjectCount(resource));
		}

		summary.setProcessingSummaries(applicationSummaryService.getProcessingSummaries(resource));
		return summary;
	}

	public Set<Integer> getAssignedResources(User user, PrismScope scopeId, List<PrismScope> parentScopeIds) {
		return getAssignedResources(user, scopeId, parentScopeIds, new ResourceListFilterDTO(), null, null);
	}

	public Set<Integer> getAssignedResources(User user, PrismScope scopeId, List<PrismScope> parentScopeIds, ResourceListFilterDTO filter) {
		return getAssignedResources(user, scopeId, parentScopeIds, filter, null, null);
	}

	public Set<Integer> getAssignedResources(User user, PrismScope scopeId, List<PrismScope> parentScopeIds, ResourceListFilterDTO filter,
	        String lastSequenceIdentifier, Integer recordsToRetrieve) {
		Set<Integer> assigned = Sets.newHashSet();
		Junction conditions = getFilterConditions(scopeId, filter);
		assigned.addAll(resourceDAO.getAssignedResources(user, scopeId, filter, conditions, lastSequenceIdentifier, recordsToRetrieve));
		for (PrismScope parentScopeId : parentScopeIds) {
			assigned.addAll(resourceDAO.getAssignedResources(user, scopeId, parentScopeId, filter, conditions, lastSequenceIdentifier, recordsToRetrieve));
		}
		return assigned;
	}

	public List<WorkflowPropertyConfigurationRepresentation> getWorkflowPropertyConfigurations(Resource resource) {
		switch (resource.getResourceScope()) {
		case APPLICATION:
			return applicationService.getWorkflowPropertyConfigurations((Application) resource);
		default:
			return (List<WorkflowPropertyConfigurationRepresentation>) (List<?>) customizationService.getConfigurationRepresentationsWithOrWithoutVersion(
			        PrismConfiguration.WORKFLOW_PROPERTY, resource, resource.getWorkflowPropertyConfigurationVersion());
		}
	}

	public SocialMetadataDTO getSocialMetadata(PrismScope resourceScope, Integer resourceId) {
		Resource resource = getNotNullResource(resourceScope, resourceId);
		switch (resourceScope) {
		case INSTITUTION:
			return institutionService.getSocialMetadata((Institution) resource);
		case PROGRAM:
			return programService.getSocialMetadata((Program) resource);
		case PROJECT:
			return projectService.getSocialMetadata((Project) resource);
		case SYSTEM:
			return systemService.getSocialMetadata();
		default:
			throw new Error();
		}
	}

	public SearchEngineAdvertDTO getSearchEngineAdvert(PrismScope resourceScope, Integer resourceId) {
		switch (resourceScope) {
		case INSTITUTION:
			return institutionService.getSearchEngineAdvert(resourceId);
		case PROGRAM:
			return programService.getSearchEngineAdvert(resourceId);
		case PROJECT:
			return projectService.getSearchEngineAdvert(resourceId);
		case SYSTEM:
			return systemService.getSearchEngineAdvert();
		default:
			throw new Error();
		}
	}

	private Resource getNotNullResource(PrismScope resourceScope, Integer resourceId) throws Error {
		Resource resource = getById(resourceScope.getResourceClass(), resourceId);
		if (resource == null) {
			throw new Error();
		}
		return resource;
	}

	public String getSocialThumbnailUrl(Resource resource) {
		String defaultSocialThumbnail = applicationUrl + "/images/fbimg.jpg";
		if (resource.getResourceScope() == PrismScope.SYSTEM) {
			return defaultSocialThumbnail;
		} else {
			Document logoDocument = resource.getInstitution().getLogoDocument();
			if (logoDocument == null) {
				return defaultSocialThumbnail;
			}
			return applicationApiUrl + "/images/" + logoDocument.getId().toString();
		}
	}

	public String getSocialResourceUrl(Resource resource) {
		return applicationUrl + "/" + PrismConstants.ANGULAR_HASH + "/?" + resource.getResourceScope().getLowerCamelName() + "=" + resource.getId();
	}

	public PrismLocale getOperativeLocale(Resource resource) {
		if (resource.getResourceScope() == PrismScope.SYSTEM) {
			User currentUser = userService.getCurrentUser();
			if (currentUser != null) {
				return currentUser.getLocale();
			}
		}
		return resource.getLocale();
	}

	public <T extends Resource> HashMultimap<PrismScope, T> getUserAdministratorResources(User user) {
		HashMultimap<PrismScope, T> userAdministratorResources = HashMultimap.create();
		for (UserAdministratorResourceDTO userAdministratorResource : resourceDAO.getUserAdministratorResources(user)) {
			T resource = (T) userAdministratorResource.getResource();
			userAdministratorResources.put(resource.getResourceScope(), resource);
		}
		userAdministratorResources.putAll(APPLICATION, (List<T>) applicationService.getUserAdministratorApplications(userAdministratorResources));
		return userAdministratorResources;
	}

	public void reassignResources(User oldUser, User newUser) {
		for (PrismScope prismScope : PrismScope.values()) {
			resourceDAO.reassignResources(prismScope, oldUser, newUser);
		}
	}

	private Junction getFilterConditions(PrismScope scopeId, ResourceListFilterDTO filter) {
		if (filter.hasConstraints()) {
			Junction conditions = Restrictions.conjunction();
			if (filter.getMatchMode() == FilterMatchMode.ANY) {
				conditions = Restrictions.disjunction();
			}

			for (ResourceListFilterConstraintDTO constraint : filter.getConstraints()) {
				ResourceListFilterProperty property = constraint.getFilterProperty();

				if (ResourceListFilterProperty.isPermittedFilterProperty(scopeId, property)) {
					String propertyName = property.getPropertyName();
					Boolean negated = BooleanUtils.toBoolean(constraint.getNegated());
					switch (property) {
					case CLOSING_DATE:
					case CONFIRMED_START_DATE:
					case DUE_DATE:
						ResourceListConstraintBuilder.appendDateFilterCriterion(conditions, propertyName, constraint.getFilterExpression(),
						        constraint.getValueDateStart(), constraint.getValueDateClose(), negated);
						break;
					case CODE:
					case REFERRER:
						ResourceListConstraintBuilder.appendStringFilterCriterion(conditions, propertyName, constraint.getValueString(), negated);
						break;
					case CREATED_TIMESTAMP:
					case UPDATED_TIMESTAMP:
						ResourceListConstraintBuilder.appendDateTimeFilterCriterion(conditions, propertyName, constraint.getFilterExpression(),
						        constraint.computeValueDateTimeStart(), constraint.computeValueDateTimeClose(), negated);
						break;
					case INSTITUTION_TITLE:
					case PROGRAM_TITLE:
					case PROJECT_TITLE:
						List<Integer> parentResourceIds = resourceDAO.getMatchingParentResources(PrismScope.valueOf(property.name().replace("_TITLE", "")),
						        constraint.getValueString());
						ResourceListConstraintBuilder.appendPropertyInFilterCriterion(conditions, propertyName, parentResourceIds, negated);
						break;
					case TITLE:
						ResourceListConstraintBuilder.appendStringFilterCriterion(conditions, propertyName, constraint.getValueString(), negated);
						break;
					case RATING:
						ResourceListConstraintBuilder.appendDecimalFilterCriterion(conditions, propertyName, constraint.getFilterExpression(),
						        constraint.getValueDecimalStart(), constraint.getValueDecimalClose(), negated);
						break;
					case STATE_GROUP_TITLE:
						List<PrismState> stateIds = stateService.getStatesByStateGroup(constraint.getValueStateGroup());
						ResourceListConstraintBuilder.appendPropertyInFilterCriterion(conditions, propertyName, stateIds, negated);
						break;
					case SUBMITTED_TIMESTAMP:
						ResourceListConstraintBuilder.appendDateTimeFilterCriterion(conditions, propertyName, constraint.getFilterExpression(),
						        constraint.computeValueDateTimeStart(), constraint.computeValueDateTimeClose(), negated);
						break;
					case USER:
						List<Integer> userIds = userService.getMatchingUsers(constraint.getValueString());
						ResourceListConstraintBuilder.appendPropertyInFilterCriterion(conditions, propertyName, userIds, negated);
						break;
					case STUDY_AREA:
					case STUDY_DIVISION:
					case STUDY_LOCATION:
					case STUDY_APPLICATION:
					case PRIMARY_THEME:
					case SECONDARY_THEME:
						ResourceListConstraintBuilder.appendStringFilterCriterion(conditions, propertyName, constraint.getValueString(), negated);
						break;
					case SUPERVISOR:
						appendUserRoleFilterCriteria(scopeId, conditions, constraint, propertyName, Arrays.asList(PrismRole.PROJECT_PRIMARY_SUPERVISOR,
						        PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.APPLICATION_SUGGESTED_SUPERVISOR, PrismRole.APPLICATION_PRIMARY_SUPERVISOR,
						        PrismRole.APPLICATION_SECONDARY_SUPERVISOR), negated);
						break;
					case PROJECT_USER:
						appendUserRoleFilterCriteria(scopeId, conditions, constraint, propertyName,
						        Arrays.asList(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.PROJECT_ADMINISTRATOR),
						        negated);
						break;
					case PROGRAM_USER:
						appendUserRoleFilterCriteria(scopeId, conditions, constraint, propertyName,
						        Arrays.asList(PrismRole.PROGRAM_ADMINISTRATOR, PrismRole.PROGRAM_APPROVER, PrismRole.PROGRAM_VIEWER), negated);
						break;
					case INSTITUTION_USER:
						appendUserRoleFilterCriteria(scopeId, conditions, constraint, propertyName,
						        Arrays.asList(PrismRole.INSTITUTION_ADMINISTRATOR, PrismRole.INSTITUTION_ADMITTER), negated);
						break;
					}
				} else {
					ResourceListConstraintBuilder.throwResourceFilterListMissingPropertyError(scopeId, property);
				}
			}

			return conditions;
		}
		return null;
	}

	private void appendUserRoleFilterCriteria(PrismScope scopeId, Junction conditions, ResourceListFilterConstraintDTO constraint, String propertyName,
	        List<PrismRole> valueRoles, Boolean negated) {
		boolean doAddCondition = false;
		Junction inCondition = negated ? Restrictions.conjunction() : Restrictions.disjunction();
		for (PrismRole valueRole : valueRoles) {
			PrismScope roleScope = valueRole.getScope();
			String actualPropertyName = scopeId == roleScope ? propertyName : roleScope.getLowerCamelName();
			List<Integer> resourceIds = resourceDAO.getByMatchingUsersInRole(scopeId, constraint.getValueString(), valueRole);
			if (!resourceIds.isEmpty()) {
				ResourceListConstraintBuilder.appendPropertyInFilterCriterion(inCondition, actualPropertyName, resourceIds, negated);
				doAddCondition = true;
			}
		}
		if (doAddCondition) {
			conditions.add(inCondition);
		}
	}

	private void createOrUpdateStateTransitionSummary(Resource resource, DateTime baselineTime) {
		String transitionStateSelection = Joiner.on("|").join(stateService.getCurrentStates(resource));

		ResourceStateTransitionSummary transientTransitionSummary = new ResourceStateTransitionSummary().withResource(resource.getParentResource())
		        .withStateGroup(resource.getPreviousState().getStateGroup()).withTransitionStateSelection(transitionStateSelection).withFrequency(1)
		        .withUpdatedTimestamp(baselineTime);
		ResourceStateTransitionSummary persistentTransitionSummary = entityService.getDuplicateEntity(transientTransitionSummary);

		if (persistentTransitionSummary == null) {
			entityService.save(transientTransitionSummary);
		} else {
			persistentTransitionSummary.setFrequency(persistentTransitionSummary.getFrequency() + 1);
			persistentTransitionSummary.setUpdatedTimestamp(baselineTime);
		}
	}

	private <T extends ResourceStateDefinition> void deleteResourceStates(Set<T> resourceStateDefinitions) {
		for (T resourceState : resourceStateDefinitions) {
			entityService.delete(resourceState);
		}
		resourceStateDefinitions.clear();
	}

	private <T extends ResourceStateDefinition, U extends CommentStateDefinition> void insertResourceStates(Resource resource, Set<T> resourceStateDefinitions,
	        Set<U> commentStateDefinitions, Class<T> resourceStateClass) throws InstantiationException, IllegalAccessException {
		for (U commentState : commentStateDefinitions) {
			T transientResourceStateDefinition = resourceStateClass.newInstance();
			transientResourceStateDefinition.setResource(resource);
			transientResourceStateDefinition.setState(commentState.getState());
			transientResourceStateDefinition.setPrimaryState(commentState.getPrimaryState());
			T persistentResourceStateDefinition = entityService.createOrUpdate(transientResourceStateDefinition);
			resourceStateDefinitions.add(persistentResourceStateDefinition);
		}
	}

}
