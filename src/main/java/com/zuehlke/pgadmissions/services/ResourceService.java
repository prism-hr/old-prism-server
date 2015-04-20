package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterMatchMode.ANY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.IMPORT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.utils.PrismConstants.LIST_PAGE_ROW_COUNT;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentStateDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
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
import com.zuehlke.pgadmissions.dto.ResourceListActionDTO;
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
import com.zuehlke.pgadmissions.services.builders.PrismResourceListConstraintBuilder;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismConstants;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

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
    private PrismResourceListConstraintBuilder resourceListConstraintBuilder;

    @Inject
    private ApplicationContext applicationContext;

    public Resource getById(PrismScope resourceScope, Integer id) {
        return entityService.getById(resourceScope.getResourceClass(), id);
    }

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
            throw new UnsupportedOperationException();
        }

        resource.setReferrer(referrer);
        resource.setWorkflowPropertyConfigurationVersion(workflowPropertyConfigurationVersion);

        Set<ResourceCondition> resourceConditions = resource.getResourceConditions();
        List<PrismActionCondition> actionConditions = actionService.getActionConditions(resourceScope);
        for (PrismActionCondition actionCondition : actionConditions) {
            ResourceCondition resourceCondition = new ResourceCondition().withResource(resource).withActionCondition(actionCondition);
            entityService.save(resourceCondition);
            resourceConditions.add(resourceCondition);
        }

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
                PrismReflectionUtils.setProperty(resource, "updatedTimestampSitemap", baseline);
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

    public ActionOutcomeDTO executeAction(Integer resourceId, CommentDTO commentDTO) throws Exception {
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

            StateDurationConfiguration stateDurationConfiguration = stateDurationDefinition == null ? null //
                    : stateService.getStateDurationConfiguration(resource, comment.getUser(), stateDurationDefinition);
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

    public void executeUpdate(Resource resource, PrismDisplayPropertyDefinition messageIndex, CommentAssignedUser... assignees) throws Exception {
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
        return Arrays.asList(CREATE_RESOURCE, IMPORT_RESOURCE).contains(action.getActionCategory()) ? resource.getParentResource() : resource;
    }

    public List<Integer> getResourcesToEscalate(PrismScope resourceScope, PrismAction actionId, LocalDate baseline) {
        return resourceDAO.getResourcesToEscalate(resourceScope, actionId, baseline);
    }

    public <T extends Resource> List<Integer> getResourcesToPropagate(PrismScope propagatingResourceScope, Integer propagatingResourceId,
            PrismScope propagatedResourceScope, PrismAction actionId) {
        return resourceDAO.getResourcesToPropagate(propagatingResourceScope, propagatingResourceId, propagatedResourceScope, actionId);
    }

    public List<Integer> getResourcesRequiringIndividualReminders(PrismScope resourceScope, LocalDate baseline) {
        return resourceDAO.getResourcesRequiringIndividualReminders(resourceScope, baseline);
    }

    public List<Integer> getResourcesRequiringSyndicatedReminders(PrismScope resourceScope, LocalDate baseline) {
        return resourceDAO.getResourcesRequiringSyndicatedReminders(resourceScope, baseline);
    }

    public List<Integer> getResourcesRequiringSyndicatedUpdates(PrismScope resourceScope, LocalDate baseline, DateTime rangeStart, DateTime rangeClose) {
        return resourceDAO.getResourceRequiringSyndicatedUpdates(resourceScope, baseline, rangeStart, rangeClose);
    }

    public List<ResourceListRowDTO> getResourceList(PrismScope resourceScope, ResourceListFilterDTO filter, String lastSequenceIdentifier) throws Exception {
        User user = userService.getCurrentUser();
        List<PrismScope> parentScopeIds = scopeService.getParentScopesDescending(resourceScope);
        filter = resourceListFilterService.saveOrGetByUserAndScope(user, resourceScope, filter);

        int maxRecords = LIST_PAGE_ROW_COUNT;
        Set<Integer> assignedResources = getAssignedResources(user, resourceScope, parentScopeIds, filter, lastSequenceIdentifier, maxRecords);

        boolean hasRedactions = actionService.hasRedactions(resourceScope, assignedResources, user);

        if (!assignedResources.isEmpty()) {
            HashMultimap<Integer, ResourceListActionDTO> creations = actionService.getCreateResourceActions(resourceScope, assignedResources);
            List<ResourceListRowDTO> rows = resourceDAO.getResourceConsoleList(user, resourceScope, parentScopeIds, assignedResources, filter,
                    lastSequenceIdentifier, maxRecords, hasRedactions);
            for (ResourceListRowDTO row : rows) {
                Set<ResourceListActionDTO> actions = Sets.newLinkedHashSet();
                actions.addAll(actionService.getPermittedActions(resourceScope, row.getSystemId(), row.getInstitutionId(), row.getProgramId(),
                        row.getProjectId(),
                        row.getApplicationId(), user));
                actions.addAll(creations.get(row.getResourceId()));
                row.setActions(actions);
            }
            return rows;
        }

        return Lists.newArrayList();
    }

    public ResourceSummaryRepresentation getResourceSummary(PrismScope resourceScope, Integer resourceId) {
        ResourceParent resource = (ResourceParent) getById(resourceScope, resourceId);

        ResourceSummaryRepresentation summary = new ResourceSummaryRepresentation().withCreatedDate(resource.getCreatedTimestamp().toLocalDate())
                .withApplicationCreatedCount(resource.getApplicationCreatedCount()).withApplicationSubmittedCount(resource.getApplicationSubmittedCount())
                .withApplicationApprovedCount(resource.getApplicationApprovedCount()).withApplicationRejectedCount(resource.getApplicationRejectedCount())
                .withApplicationWithdrawnCount(resource.getApplicationWithdrawnCount()).withApplicationRatingCount(resource.getApplicationRatingCount())
                .withApplicationRatingOccurenceAverage(resource.getApplicationRatingCountAverageNonZero());

        if (resourceScope == INSTITUTION) {
            summary.setProgramCount(programService.getActiveProgramCount((Institution) resource));
            summary.setProjectCount(projectService.getActiveProjectCount(resource));
        } else if (resourceScope == PROGRAM) {
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

        List<Integer> resources = resourceDAO.getAssignedResources(user, scopeId, filter, conditions, lastSequenceIdentifier, recordsToRetrieve);
        assigned.addAll(resources);

        for (PrismScope parentScopeId : parentScopeIds) {
            resources = resourceDAO.getAssignedResources(user, scopeId, parentScopeId, filter, conditions, lastSequenceIdentifier, recordsToRetrieve);
            assigned.addAll(resources);
        }
        return assigned;
    }

    public List<WorkflowPropertyConfigurationRepresentation> getWorkflowPropertyConfigurations(Resource resource) throws Exception {
        switch (resource.getResourceScope()) {
        case APPLICATION:
            return applicationService.getWorkflowPropertyConfigurations((Application) resource);
        default:
            return (List<WorkflowPropertyConfigurationRepresentation>) (List<?>) customizationService.getConfigurationRepresentationsWithOrWithoutVersion(
                    PrismConfiguration.WORKFLOW_PROPERTY, resource, resource.getWorkflowPropertyConfigurationVersion());
        }
    }

    public SocialMetadataDTO getSocialMetadata(PrismScope resourceScope, Integer resourceId) throws Exception {
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

    public Map<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource resource, PrismScope propertiesScope, PrismLocale locale) throws Exception {
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource, locale);
        Map<PrismDisplayPropertyDefinition, String> properties = Maps.newLinkedHashMap();
        for (PrismDisplayPropertyDefinition prismDisplayPropertyDefinition : PrismDisplayPropertyDefinition.getProperties(propertiesScope)) {
            properties.put(prismDisplayPropertyDefinition, loader.load(prismDisplayPropertyDefinition));
        }
        return properties;
    }

    private Resource getNotNullResource(PrismScope resourceScope, Integer resourceId) throws Error {
        Resource resource = getById(resourceScope.getResourceClass(), resourceId);
        return Preconditions.checkNotNull(resource);
    }

    public String getSocialThumbnailUrl(Resource resource) {
        String defaultSocialThumbnail = applicationUrl + "/images/fbimg.jpg";
        if (resource.getResourceScope() == PrismScope.SYSTEM) {
            return defaultSocialThumbnail;
        } else {
            Document logoDocument = resource.getInstitution().getAdvert().getLogoImage();
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
        return getOperativeLocale(resource, null);
    }

    public PrismLocale getOperativeLocale(Resource resource, PrismLocale prismLocale) {
        if (resource.getResourceScope() == PrismScope.SYSTEM) {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return prismLocale == null ? PrismLocale.getSystemLocale() : prismLocale;
            } else {
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

    public List<Integer> getResourcesByUserMatchingUserAndRole(PrismScope prismScope, String searchTerm, List<PrismRole> prismRoles) {
        return resourceDAO.getResourcesByMatchingUsersAndRole(prismScope, searchTerm, prismRoles);
    }

    public List<Integer> getResourcesByMatchingEnclosingResource(PrismScope enclosingResourceScope, String searchTerm) {
        return resourceDAO.getResourcesByMatchingEnclosingResources(enclosingResourceScope, searchTerm);
    }
    
    public DateTime getResourcePublishedTimestamp(ResourceParent resource) {
        return resourceDAO.getResourcePublishedTimestamp(resource);
    }

    private Junction getFilterConditions(PrismScope resourceScope, ResourceListFilterDTO filter) {
        Junction conditions = null;
        if (filter.hasConstraints()) {
            conditions = filter.getMatchMode() == ANY ? Restrictions.disjunction() : Restrictions.conjunction();
            for (ResourceListFilterConstraintDTO constraint : filter.getConstraints()) {
                resourceListConstraintBuilder.appendFilter(conditions, resourceScope, constraint);
            }
        }
        return conditions;
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
