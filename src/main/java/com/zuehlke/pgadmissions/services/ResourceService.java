package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterMatchMode.ANY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.IMPORT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.utils.PrismConstants.LIST_PAGE_ROW_COUNT;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.doubleToBigDecimal;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.longToInteger;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentState;
import com.zuehlke.pgadmissions.domain.comment.CommentStateDefinition;
import com.zuehlke.pgadmissions.domain.comment.CommentTransitionState;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceCondition;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.resource.ResourceStateDefinition;
import com.zuehlke.pgadmissions.domain.resource.ResourceStateTransitionSummary;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyLocation;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;
import com.zuehlke.pgadmissions.dto.ResourceListActionDTO;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SocialMetadataDTO;
import com.zuehlke.pgadmissions.dto.UserAdministratorResourceDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionPartnerDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceParentDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceParentDTO.ResourceConditionDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceParentDTO.ResourceParentAttributesDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation.ApplicationProcessingSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation.ApplicationProcessingSummaryRepresentationMonth;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation.ApplicationProcessingSummaryRepresentationYear;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.builders.PrismResourceListConstraintBuilder;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismConstants;
import com.zuehlke.pgadmissions.utils.ToPropertyFunction;
import com.zuehlke.pgadmissions.workflow.executors.action.ActionExecutor;
import com.zuehlke.pgadmissions.workflow.resource.seo.search.SearchRepresentationBuilder;
import com.zuehlke.pgadmissions.workflow.resource.seo.social.SocialRepresentationBuilder;
import com.zuehlke.pgadmissions.workflow.transition.creators.ResourceCreator;
import com.zuehlke.pgadmissions.workflow.transition.persisters.ResourcePersister;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Service
@Transactional
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
    private AdvertService advertService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ProjectService projectService;

    @Inject
    private ProgramService programService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ImportedEntityService importedEntityService;

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

    public ActionOutcomeDTO create(User user, Action action, ResourceDTO resourceDTO, String referrer, Integer workflowPropertyConfigurationVersion)
            throws Exception {
        PrismScope resourceScope = action.getCreationScope().getId();

        Class<? extends ResourceCreator> resourceCreator = resourceScope.getResourceCreator();
        if (resourceCreator == null) {
            throw new UnsupportedOperationException();
        }

        User resourceUser = user.getParentUser();
        Resource resource = applicationContext.getBean(resourceCreator).create(user, resourceDTO);

        resource.setReferrer(referrer);
        resource.setWorkflowPropertyConfigurationVersion(workflowPropertyConfigurationVersion);

        Comment comment = new Comment().withUser(resourceUser).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                .addAssignedUser(resourceUser, roleService.getCreatorRole(resource), CREATE);
        return actionService.executeUserAction(resource, action, comment);
    }

    public void persistResource(Resource resource, Comment comment) throws Exception {
        if (comment.isCreateComment()) {
            DateTime baseline = new DateTime();
            resource.setCreatedTimestamp(baseline);
            resource.setUpdatedTimestamp(baseline);

            if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
                ResourceParent parent = (ResourceParent) resource;
                parent.setUpdatedTimestampSitemap(baseline);
            }

            Class<? extends ResourcePersister> resourcePersister = resource.getResourceScope().getResourcePersister();
            if (resourcePersister == null) {
                throw new UnsupportedOperationException();
            }

            applicationContext.getBean(resourcePersister).persist(resource);

            resource.setCode(generateResourceCode(resource));
            entityService.save(resource);
            entityService.flush();
        }
    }

    public ActionOutcomeDTO executeAction(Integer resourceId, CommentDTO commentDTO) throws Exception {
        Class<? extends ActionExecutor> actionExecutor = commentDTO.getAction().getScope().getActionExecutor();
        if (actionExecutor == null) {
            throw new UnsupportedOperationException();
        }
        return applicationContext.getBean(actionExecutor).execute(resourceId, commentDTO);
    }

    public void preProcessResource(Resource resource, Comment comment) throws Exception {
        Class<? extends ResourceProcessor> processor = resource.getResourceScope().getResourcePreprocessor();
        if (processor != null) {
            applicationContext.getBean(processor).process(resource, comment);
        }
    }

    public void recordStateTransition(Resource resource, Comment comment, State state, State transitionState) throws DeduplicationException,
            InstantiationException, IllegalAccessException {
        resource.setPreviousState(state);
        resource.setState(transitionState);

        Set<ResourcePreviousState> resourcePreviousStates = resource.getResourcePreviousStates();
        Set<CommentState> commentStates = comment.getCommentStates();
        deleteResourceStates(resourcePreviousStates, commentStates);

        Set<ResourceState> resourceStates = resource.getResourceStates();
        Set<CommentTransitionState> commentTransitionStates = comment.getCommentTransitionStates();
        deleteResourceStates(resourceStates, commentTransitionStates);
        entityService.flush();

        LocalDate baseline = comment.getCreatedTimestamp().toLocalDate();
        insertResourceStates(resource, resourcePreviousStates, commentStates, ResourcePreviousState.class, baseline);
        insertResourceStates(resource, resourceStates, commentTransitionStates, ResourceState.class, baseline);
        entityService.flush();
    }

    public void processResource(Resource resource, Comment comment) throws Exception {
        Class<? extends ResourceProcessor> processor = resource.getResourceScope().getResourceProcessor();
        if (processor != null) {
            applicationContext.getBean(processor).process(resource, comment);
        }

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

    public void postProcessResource(Resource resource, Comment comment) throws Exception {
        DateTime baselineTime = new DateTime();

        if (comment.isUserComment() || resource.getSequenceIdentifier() == null) {
            resource.setUpdatedTimestamp(baselineTime);
            resource.setSequenceIdentifier(Long.toString(baselineTime.getMillis()) + String.format("%010d", resource.getId()));
        }

        Class<? extends ResourceProcessor> processor = resource.getResourceScope().getResourcePostprocessor();
        if (processor != null) {
            entityService.flush();
            applicationContext.getBean(processor).process(resource, comment);
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

    public Resource getOperativeResource(Resource resource, Action action) {
        return Arrays.asList(CREATE_RESOURCE, IMPORT_RESOURCE).contains(action.getActionCategory()) ? resource.getParentResource() : resource;
    }

    public List<Integer> getResourcesToEscalate(PrismScope resourceScope, PrismAction actionId, LocalDate baseline) {
        return resourceDAO.getResourcesToEscalate(resourceScope, actionId, baseline);
    }

    public Set<Integer> getResourcesToPropagate(PrismScope propagatingScope, Integer propagatingId, PrismScope propagatedScope, PrismAction actionId) {
        Set<Integer> resources = Sets.newHashSet(resourceDAO.getResourcesToPropagate(propagatingScope, propagatingId, propagatedScope,
                actionId));
        resources.addAll(resourceDAO.getPartnerResourcesToPropagate(propagatingScope, propagatingId, propagatedScope, actionId));
        return resources;
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
            List<ResourceListRowDTO> rows = resourceDAO.getResourceList(user, resourceScope, parentScopeIds, assignedResources, filter,
                    lastSequenceIdentifier, maxRecords, hasRedactions);
            for (ResourceListRowDTO row : rows) {
                Set<ResourceListActionDTO> actions = Sets.newLinkedHashSet();
                actions.addAll(actionService.getPermittedActions(resourceScope, row, user));
                actions.addAll(creations.get(row.getResourceId()));
                row.setActions(actions);
            }
            return rows;
        }

        return Lists.newArrayList();
    }

    public ResourceSummaryRepresentation getResourceSummary(PrismScope resourceScope, Integer resourceId) throws Exception {
        ResourceParent resource = (ResourceParent) getById(resourceScope, resourceId);

        ResourceSummaryRepresentation summary = new ResourceSummaryRepresentation().withCreatedDate(resource.getCreatedTimestamp().toLocalDate());

        if (resourceScope == INSTITUTION) {
            summary.setProgramCount(programService.getActiveProgramCount((Institution) resource));
            summary.setProjectCount(projectService.getActiveProjectCount(resource));
        } else if (resourceScope == PROGRAM) {
            summary.setProjectCount(projectService.getActiveProjectCount(resource));
        }

        boolean currentYear = true;
        List<ApplicationProcessingSummaryRepresentationYear> yearRepresentations = Lists.newLinkedList();
        List<ApplicationProcessingSummaryDTO> yearSummaries = applicationService.getApplicationProcessingSummariesByYear(resourceScope, resourceId);
        LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> monthSummaries = applicationService.getApplicationProcessingSummariesByMonth(resourceScope,
                resourceId);
        for (ApplicationProcessingSummaryDTO yearSummary : yearSummaries) {
            ApplicationProcessingSummaryRepresentationYear yearRepresentation = new ApplicationProcessingSummaryRepresentationYear();
            String applicationYear = yearSummary.getApplicationYear();
            yearRepresentation.setApplicationYear(applicationYear);

            if (currentYear == true) {
                Integer monthOfBusinessYear = institutionService.getMonthOfBusinessYear(resource.getInstitution(), new LocalDate().getMonthOfYear());
                yearRepresentation.setPercentageComplete(new BigDecimal(monthOfBusinessYear).divide(new BigDecimal(12), 2, RoundingMode.HALF_UP));
            } else {
                yearRepresentation.setPercentageComplete(new BigDecimal(100.00));
            }

            populateApplicationProcessingSummary(yearSummary, yearRepresentation);

            List<ApplicationProcessingSummaryRepresentationMonth> monthRepresentations = Lists.newLinkedList();
            for (ApplicationProcessingSummaryDTO monthSummary : monthSummaries.get(applicationYear)) {
                ApplicationProcessingSummaryRepresentationMonth monthRepresentation = new ApplicationProcessingSummaryRepresentationMonth();
                monthSummary.setApplicationMonth(monthSummary.getApplicationMonth());
                populateApplicationProcessingSummary(monthSummary, monthRepresentation);
                monthRepresentations.add(monthRepresentation);
            }

            yearRepresentation.setProcessingSummaries(monthRepresentations);
            currentYear = false;
        }

        summary.setProcessingSummaries(yearRepresentations);
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

        assigned.addAll(resourceDAO.getAssignedPartnerResources(user, scopeId, filter, conditions, lastSequenceIdentifier, recordsToRetrieve));
        return assigned;
    }

    @SuppressWarnings("unchecked")
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
        Class<? extends SocialRepresentationBuilder> socialRepresentationBuilder = resourceScope.getSocialRepresentationBuilder();
        if (socialRepresentationBuilder == null) {
            throw new UnsupportedOperationException();
        }
        Resource resource = getNotNullResource(resourceScope, resourceId);
        return applicationContext.getBean(socialRepresentationBuilder).build(resource.getAdvert());
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(PrismScope resourceScope, Integer resourceId) throws Exception {
        Class<? extends SearchRepresentationBuilder> searchRepresentationBuilder = resourceScope.getSearchRepresentationBuilder();
        if (searchRepresentationBuilder == null) {
            throw new UnsupportedOperationException();
        }
        return applicationContext.getBean(searchRepresentationBuilder).build(resourceId);
    }

    public Map<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource resource, PrismScope propertiesScope) throws Exception {
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource);
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
            Document logoImage = resource.getInstitution().getLogoImage();
            if (logoImage == null) {
                return defaultSocialThumbnail;
            }
            return applicationApiUrl + "/images/" + logoImage.getId().toString();
        }
    }

    public String getSocialResourceUrl(Resource resource) {
        return applicationUrl + "/" + PrismConstants.ANGULAR_HASH + "/?" + resource.getResourceScope().getLowerCamelName() + "=" + resource.getId();
    }

    @SuppressWarnings("unchecked")
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

    public List<PrismAction> getPartnerActions(ResourceParent resource) {
        List<PrismActionCondition> filteredActionConditions = Lists.newLinkedList();
        List<ResourceCondition> actionConditions = resourceDAO
                .getResourceAttributes(resource, ResourceCondition.class, "actionCondition", null);

        PrismScope lastResourceScope = null;
        for (ResourceCondition actionCondition : actionConditions) {
            PrismScope thisResourceScope = actionCondition.getResource().getResourceScope();
            if (lastResourceScope != null && !thisResourceScope.equals(lastResourceScope)) {
                break;
            }
            filteredActionConditions.add(actionCondition.getActionCondition());
            lastResourceScope = thisResourceScope;
        }

        Resource lastResource = resource.getEnclosingResource(lastResourceScope);
        List<PrismAction> partnerActions = actionService.getPartnerActions(lastResource, filteredActionConditions);
        return partnerActions; 
    }

    public ResourceStudyOption getStudyOption(ResourceOpportunity resource, StudyOption studyOption) {
        if (resource.getResourceScope() == PROGRAM) {
            Program program = resource.getProgram();
            if (BooleanUtils.isTrue(program.getImported())) {
                return resourceDAO.getResourceAttributeStrict(resource, ResourceStudyOption.class, "studyOption", studyOption);
            }
        }

        return resourceDAO.getResourceAttribute(resource, ResourceStudyOption.class, "studyOption", studyOption);
    }

    public List<PrismStudyOption> getStudyOptions(ResourceOpportunity resource) {
        if (resource.getResourceScope() == PROGRAM) {
            Program program = resource.getProgram();
            if (BooleanUtils.isTrue(program.getImported())) {
                List<ResourceStudyOption> studyOptions = resourceDAO.getResourceAttributesStrict(resource, ResourceStudyOption.class, "studyOption", "id");
                return Lists.transform(studyOptions, Functions.compose(
                        new ToPropertyFunction<StudyOption, PrismStudyOption>("prismStudyOption"),
                        new ToPropertyFunction<ResourceStudyOption, StudyOption>("studyOption")));
            }
        }

        List<PrismStudyOption> filteredStudyOptions = Lists.newLinkedList();
        List<ResourceStudyOption> studyOptions = resourceDAO.getResourceAttributes(resource, ResourceStudyOption.class, "studyOption", "id");

        PrismScope lastResourceScope = null;
        for (ResourceStudyOption studyOption : studyOptions) {
            PrismScope thisResourceScope = studyOption.getResource().getResourceScope();
            if (lastResourceScope != null && !thisResourceScope.equals(lastResourceScope)) {
                break;
            }
            filteredStudyOptions.add(studyOption.getStudyOption().getPrismStudyOption());
            lastResourceScope = thisResourceScope;
        }

        return filteredStudyOptions;
    }

    public ResourceStudyOptionInstance getFirstStudyOptionInstance(ResourceParent resource, StudyOption studyOption) {
        return resourceDAO.getFirstStudyOptionInstance(resource, studyOption);
    }

    public List<ResourceStudyLocation> getStudyLocations(ResourceParent resource) {
        List<ResourceStudyLocation> filteredStudylocations = Lists.newLinkedList();
        List<ResourceStudyLocation> studyLocations = resourceDAO.getResourceAttributes(resource, ResourceStudyLocation.class, "studyLocation", null);

        PrismScope lastResourceScope = null;
        for (ResourceStudyLocation studyLocation : studyLocations) {
            PrismScope thisResourceScope = studyLocation.getResource().getResourceScope();
            if (lastResourceScope != null && !thisResourceScope.equals(lastResourceScope)) {
                break;
            }
            filteredStudylocations.add(studyLocation);
            lastResourceScope = thisResourceScope;
        }

        return filteredStudylocations;
    }

    public void setAttributes(ResourceParent resource, ResourceParentAttributesDTO attributes) {
        setResourceConditions(resource, attributes.getResourceConditions());
        setStudyLocations(resource, attributes.getStudyLocations());
    }

    public void setResourceConditions(ResourceParent resource, List<ResourceParentDTO.ResourceConditionDTO> resourceConditions) {
        resource.getResourceConditions().clear();
        entityService.flush();

        if (resourceConditions == null) {
            List<PrismResourceCondition> defaultResourceConditions;
            PrismScope resourceScope = resource.getResourceScope();
            if (resourceScope.equals(INSTITUTION)) {
                defaultResourceConditions = PrismOpportunityType.getResourceConditions(resourceScope);
            } else {
                PrismOpportunityType opportunityType = resource.getOpportunityType().getPrismOpportunityType();
                defaultResourceConditions = PrismOpportunityType.getResourceConditions(resourceScope, opportunityType);
            }

            for (PrismResourceCondition defaultResourceCondition : defaultResourceConditions) {
                resource.addResourceCondition(new ResourceCondition().withResource(resource)
                        .withActionCondition(defaultResourceCondition.getActionCondition())
                        .withPartnerNode(defaultResourceCondition.isPartnerMode()));
            }
        } else {
            for (ResourceParentDTO.ResourceConditionDTO resourceCondition : resourceConditions) {
                resource.addResourceCondition(new ResourceCondition().withResource(resource)
                        .withActionCondition(resourceCondition.getActionCondition())
                        .withPartnerNode(resourceCondition.getPartnerMode()));
            }
        }
    }

    public void setStudyOptions(ResourceOpportunity resource, List<PrismStudyOption> prismStudyOptions, LocalDate baseline) {
        resource.getStudyOptions().clear();
        entityService.flush();

        LocalDate close = resource.getEndDate();
        if (prismStudyOptions == null) {
            PrismScope resourceScope = resource.getResourceScope();
            if (!resourceScope.equals(INSTITUTION)) {
                prismStudyOptions = resource.getOpportunityType().getPrismOpportunityType().getDefaultStudyOptions();
            }
        }

        for (PrismStudyOption prismStudyOption : prismStudyOptions) {
            if (close.isAfter(baseline)) {
                StudyOption studyOption = importedEntityService.getByCode(StudyOption.class, resource.getInstitution(), prismStudyOption.name());
                resource.addStudyOption(new ResourceStudyOption().withResource(resource).withStudyOption(studyOption).withApplicationStartDate(baseline)
                        .withApplicationCloseDate(close));
            }
        }
    }

    public void setStudyLocations(ResourceParent resource, List<String> studyLocations) {
        resource.getStudyLocations().clear();
        entityService.flush();

        if (studyLocations != null) {
            for (String studyLocation : studyLocations) {
                resource.addStudyLocation(new ResourceStudyLocation().withResource(resource).withStudyLocation(studyLocation));
            }
        }
    }

    public void update(PrismScope resourceScope, Integer resourceId, OpportunityDTO resourceDTO, Comment comment) throws Exception {
        ResourceOpportunity resource = (ResourceOpportunity) getById(resourceScope, resourceId);
        updatePartner(comment.getUser(), resource, resourceDTO);

        DepartmentDTO departmentDTO = resourceDTO.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(departmentDTO);
        resource.setDepartment(department);

        AdvertDTO advertDTO = resourceDTO.getAdvert();
        Advert advert = resource.getAdvert();
        advertService.updateAdvert(advertDTO, advert);
        resource.setTitle(advert.getTitle());

        resource.setDurationMinimum(resourceDTO.getDurationMinimum());
        resource.setDurationMaximum(resourceDTO.getDurationMaximum());

        ResourceParentAttributesDTO attributes = resourceDTO.getAttributes();
        List<ResourceConditionDTO> resourceConditions = attributes.getResourceConditions();
        setResourceConditions(resource, resourceConditions == null ? Lists.<ResourceConditionDTO> newArrayList() : resourceConditions);
        setStudyLocations(resource, attributes.getStudyLocations());

        if (!resource.getImported()) {
            OpportunityType opportunityType = importedEntityService.getByCode(OpportunityType.class, //
                    resource.getInstitution(), resourceDTO.getOpportunityType().name());
            resource.setOpportunityType(opportunityType);

            LocalDate endDate = resourceDTO.getEndDate();
            if (endDate != null) {
                resource.setEndDate(endDate);
            }

            List<PrismStudyOption> studyOptions = resourceDTO.getStudyOptions();
            setStudyOptions(resource, studyOptions == null ? Lists.<PrismStudyOption> newArrayList() : studyOptions, new LocalDate());
        }
    }

    public void updatePartner(User user, ResourceOpportunity resource, OpportunityDTO newResource) throws Exception {
        InstitutionPartnerDTO partnerDTO = newResource.getPartner();
        if (partnerDTO != null) {
            Integer partnerId = partnerDTO.getPartnerId();
            InstitutionDTO newPartnerDTO = partnerDTO.getPartner();
            if (newPartnerDTO != null) {
                Institution partner = institutionService.createPartner(user, partnerDTO.getPartner());
                resource.setPartner(partner);
            } else if (partnerId != null) {
                Institution partner = institutionService.getById(partnerId);
                if (partner == null) {
                    throw new WorkflowEngineException("Invalid partner institution");
                }
                resource.setPartner(partner);
            }
        }
    }

    public void deleteElapsedStudyOptions() {
        LocalDate baseline = new LocalDate();
        resourceDAO.deleteElapsedStudyOptionInstances(baseline);
        resourceDAO.deleteElapsedStudyOptions(baseline);
    }

    public void synchronizePartner(Resource resource, Comment comment) {
        Institution partner = resource.getPartner();
        comment.setPartner(partner == null ? resource.getParentResource().getPartner() : partner);
    }

    public void resynchronizePartner(ResourceOpportunity resource, Comment comment) {
        Institution newPartner = resource.getPartner();
        Institution oldPartner = resourceDAO.getPreviousPartner(resource);
        if (oldPartner == null) {
            comment.setPartner(newPartner);
        } else if (!(oldPartner == null || newPartner == null) && !oldPartner.getId().equals(newPartner.getId())) {
            comment.setPartner(newPartner);
        }
    }

    public List<Integer> getResourcesByPartner(PrismScope scope, String searchTerm) {
        return resourceDAO.getResourcesByPartner(scope, searchTerm);
    }

    public List<Integer> getResourcesBySponsor(PrismScope scope, String searchTerm) {
        return resourceDAO.getResourcesBySponsor(scope, searchTerm);
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

    private <T extends ResourceStateDefinition, U extends CommentStateDefinition> void deleteResourceStates(Set<T> resourceStateDefinitions,
            Set<U> commentStateDefinitions) {
        List<State> preservedStates = Lists.newArrayListWithCapacity(commentStateDefinitions.size());
        for (CommentStateDefinition commentStateDefinition : commentStateDefinitions) {
            preservedStates.add(commentStateDefinition.getState());
        }

        for (T resourceState : resourceStateDefinitions) {
            if (!preservedStates.contains(resourceState.getState())) {
                entityService.delete(resourceState);
            }
        }
        resourceStateDefinitions.clear();
    }

    private <T extends ResourceStateDefinition, U extends CommentStateDefinition> void insertResourceStates(Resource resource, Set<T> resourceStateDefinitions,
            Set<U> commentStateDefinitions, Class<T> resourceStateClass, LocalDate baseline) throws InstantiationException, IllegalAccessException {
        for (U commentState : commentStateDefinitions) {
            T transientResourceStateDefinition = resourceStateClass.newInstance();
            transientResourceStateDefinition.setResource(resource);
            transientResourceStateDefinition.setState(commentState.getState());

            T persistentResourceStateDefinition = entityService.getDuplicateEntity(transientResourceStateDefinition);
            if (persistentResourceStateDefinition == null) {
                transientResourceStateDefinition.setPrimaryState(commentState.getPrimaryState());
                transientResourceStateDefinition.setCreatedDate(baseline);
                entityService.save(transientResourceStateDefinition);

                if (transientResourceStateDefinition.getClass().equals(ResourceState.class)) {
                    resource.addResourceState((ResourceState) transientResourceStateDefinition);
                } else {
                    resource.addResourcePreviousState((ResourcePreviousState) transientResourceStateDefinition);
                }
            }
        }
    }

    private void populateApplicationProcessingSummary(ApplicationProcessingSummaryDTO yearSummary,
            ApplicationProcessingSummaryRepresentation yearRepresentation) {
        yearRepresentation.setAdvertCount(longToInteger(yearSummary.getAdvertCount()));
        yearRepresentation.setCreatedApplicationCount(longToInteger(yearSummary.getCreatedApplicationCount()));
        yearRepresentation.setSubmittedApplicationCount(longToInteger(yearSummary.getSubmittedApplicationCount()));
        yearRepresentation.setApprovedApplicationCount(longToInteger(yearSummary.getApprovedApplicationCount()));
        yearRepresentation.setRejectedApplicationCount(longToInteger(yearSummary.getRejectedApplicationCount()));
        yearRepresentation.setWithdrawnApplicationCount(longToInteger(yearSummary.getWithdrawnApplicationCount()));
        yearRepresentation.setCreatedApplicationRatio(doubleToBigDecimal(yearSummary.getCreatedApplicationRatio(), 2));
        yearRepresentation.setSubmittedApplicationRatio(doubleToBigDecimal(yearSummary.getSubmittedApplicationRatio(), 2));
        yearRepresentation.setApprovedApplicationRatio(doubleToBigDecimal(yearSummary.getApprovedApplicationRatio(), 2));
        yearRepresentation.setRejectedApplicationRatio(doubleToBigDecimal(yearSummary.getRejectedApplicationRatio(), 2));
        yearRepresentation.setWithdrawnApplicationRatio(doubleToBigDecimal(yearSummary.getWithdrawnApplicationRatio(), 2));
        yearRepresentation.setAverageRating(doubleToBigDecimal(yearSummary.getAverageRating(), 2));
        yearRepresentation.setAveragePreparationTime(doubleToBigDecimal(yearSummary.getAveragePreparationTime(), 2));
        yearRepresentation.setAverageProcessingTime(doubleToBigDecimal(yearSummary.getAverageProcessingTime(), 2));
    }

}
