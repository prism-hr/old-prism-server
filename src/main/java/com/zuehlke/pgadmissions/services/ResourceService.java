package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.WORKFLOW_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismFilterMatchMode.ANY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.IMPORT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.utils.PrismConstants.LIST_PAGE_ROW_COUNT;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentState;
import com.zuehlke.pgadmissions.domain.comment.CommentStateDefinition;
import com.zuehlke.pgadmissions.domain.comment.CommentTransitionState;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceCondition;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceParentDivision;
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
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.dto.UserAdministratorResourceDTO;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.DepartmentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDTO.ResourceConditionDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDivisionDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationIdentity;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobotMetadata;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobotMetadataRelated;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSitemap;
import com.zuehlke.pgadmissions.services.builders.PrismResourceListConstraintBuilder;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.services.helpers.concurrency.ActionServiceHelperConcurrency;
import com.zuehlke.pgadmissions.services.helpers.concurrency.ResourceServiceHelperConcurrency;
import com.zuehlke.pgadmissions.services.helpers.concurrency.StateServiceHelperConcurrency;
import com.zuehlke.pgadmissions.workflow.executors.action.ActionExecutor;
import com.zuehlke.pgadmissions.workflow.resolvers.state.duration.StateDurationResolver;
import com.zuehlke.pgadmissions.workflow.transition.creators.ResourceCreator;
import com.zuehlke.pgadmissions.workflow.transition.populators.ResourcePopulator;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Service
@Transactional
public class ResourceService {

    @Inject
    private ResourceDAO resourceDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private CommentService commentService;

    @Inject
    private DocumentService documentService;

    @Inject
    private DepartmentService departmentService;

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
    private SystemService systemService;

    @Inject
    private UserService userService;

    @Inject
    private CustomizationService customizationService;

    @Inject
    private PrismResourceListConstraintBuilder resourceListConstraintBuilder;

    @Inject
    private ApplicationContext applicationContext;

    public Resource<?> getById(PrismScope resourceScope, Integer id) {
        return entityService.getById(resourceScope.getResourceClass(), id);
    }

    public <T extends Resource<?>> T getById(Class<T> resourceClass, Integer id) {
        return entityService.getById(resourceClass, id);
    }

    @SuppressWarnings("unchecked")
    public <T extends ResourceCreationDTO> ActionOutcomeDTO createResource(User user, Action action, T resourceDTO) throws Exception {
        PrismScope creationScope = action.getCreationScope().getId();

        ResourceCreator<T> resourceCreator = (ResourceCreator<T>) applicationContext.getBean(creationScope.getResourceCreator());
        Resource<?> resource = resourceCreator.create(user, resourceDTO);

        resource.setWorkflowPropertyConfigurationVersion(resourceDTO.getWorkflowPropertyConfigurationVersion());

        Comment comment = new Comment().withResource(resource).withUser(user).withAction(action).withDeclinedResponse(false)
                .withCreatedTimestamp(new DateTime()).addAssignedUser(user, roleService.getCreatorRole(resource), CREATE);

        return actionService.executeUserAction(resource, action, comment);
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource<?>> void persistResource(T resource, Comment comment) {
        DateTime baseline = new DateTime();
        if (comment.isCreateComment()) {
            resource.setCreatedTimestamp(baseline);
            setResourceUpdated(resource, baseline);

            if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
                ResourceParent<?> parent = (ResourceParent<?>) resource;
                parent.setUpdatedTimestampSitemap(baseline);
            }

            Class<? extends ResourcePopulator<T>> populator = (Class<? extends ResourcePopulator<T>>) resource.getResourceScope().getResourcePopulator();
            if (populator != null) {
                applicationContext.getBean(populator).populate(resource);
            }

            entityService.save(resource);

            resource.setCode(generateResourceCode(resource));
            if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
                resource.getAdvert().setResource(resource);
            }

            Integer workflowPropertyConfigurationVersion = resource.getWorkflowPropertyConfigurationVersion();
            if (workflowPropertyConfigurationVersion == null) {
                customizationService.getActiveConfigurationVersion(WORKFLOW_PROPERTY, resource);
            }

            entityService.flush();
        } else if (comment.isUserComment() || resource.getSequenceIdentifier() == null) {
            setResourceUpdated(resource, baseline);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ResourceCreationDTO> ActionOutcomeDTO executeAction(User user, Integer resourceId, CommentDTO commentDTO) throws Exception {
        if (commentDTO.getAction().getActionCategory().equals(CREATE_RESOURCE)) {
            T resourceDTO = (T) commentDTO.getResource();
            Action action = actionService.getById(commentDTO.getAction());
            resourceDTO.setParentResource(commentDTO.getResource().getParentResource());
            return createResource(user, action, resourceDTO);
        }

        Class<? extends ActionExecutor> actionExecutor = commentDTO.getAction().getScope().getActionExecutor();
        if (actionExecutor != null) {
            return applicationContext.getBean(actionExecutor).execute(resourceId, commentDTO);
        }

        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource<?>> void preProcessResource(T resource, Comment comment) throws Exception {
        Class<? extends ResourceProcessor<T>> processor = (Class<? extends ResourceProcessor<T>>) resource.getResourceScope().getResourcePreprocessor();
        if (processor != null) {
            applicationContext.getBean(processor).process(resource, comment);
        }
    }

    public void recordStateTransition(Resource<?> resource, Comment comment, State state, State transitionState) throws Exception {
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

    @SuppressWarnings("unchecked")
    public <T extends Resource<?>> void processResource(T resource, Comment comment) throws Exception {
        Class<? extends ResourceProcessor<T>> processor = (Class<? extends ResourceProcessor<T>>) resource.getResourceScope().getResourceProcessor();
        if (processor != null) {
            applicationContext.getBean(processor).process(resource, comment);
        }

        StateDurationDefinition stateDurationDefinition = resource.getState().getStateDurationDefinition();
        if (comment.isStateTransitionComment() || (stateDurationDefinition != null && BooleanUtils.isTrue(stateDurationDefinition.getEscalation()))) {
            LocalDate baselineCustom = null;
            LocalDate baseline = new LocalDate();

            PrismStateDurationEvaluation stateDurationEvaluation = resource.getState().getStateDurationEvaluation();
            if (stateDurationEvaluation != null) {
                StateDurationResolver<T> resolver = (StateDurationResolver<T>) applicationContext.getBean(stateDurationEvaluation.getResolver());
                baselineCustom = resolver.resolve(resource, comment);
            }

            baseline = baselineCustom == null || baselineCustom.isBefore(baseline) ? baseline : baselineCustom;

            StateDurationConfiguration stateDurationConfiguration = stateDurationDefinition == null ? null //
                    : stateService.getStateDurationConfiguration(resource, comment.getUser(), stateDurationDefinition);
            resource.setDueDate(baseline.plusDays(stateDurationConfiguration == null ? 0 : stateDurationConfiguration.getDuration()));

            entityService.flush();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource<?>> void postProcessResource(T resource, Comment comment) throws Exception {
        DateTime baselineTime = new DateTime();

        Class<? extends ResourceProcessor<T>> processor = (Class<? extends ResourceProcessor<T>>) resource.getResourceScope().getResourcePostprocessor();
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

    public Comment executeUpdate(Resource<?> resource, PrismDisplayPropertyDefinition messageIndex, CommentAssignedUser... assignees) throws Exception {
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
        return comment;
    }

    public <T extends Resource<?>> T getOperativeResource(T resource, Action action) {
        return Arrays.asList(CREATE_RESOURCE, IMPORT_RESOURCE).contains(action.getActionCategory()) ? resource.getParentResource() : resource;
    }

    public List<Integer> getResourcesToEscalate(PrismScope resourceScope, PrismAction actionId, LocalDate baseline) {
        return resourceDAO.getResourcesToEscalate(resourceScope, actionId, baseline);
    }

    public List<Integer> getResourcesToPropagate(PrismScope propagatingScope, Integer propagatingId, PrismScope propagatedScope, PrismAction actionId) {
        return resourceDAO.getResourcesToPropagate(propagatingScope, propagatingId, propagatedScope, actionId);
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

    public List<ResourceListRowDTO> getResourceList(final PrismScope resourceScope, ResourceListFilterDTO filter, String lastSequenceIdentifier)
            throws Exception {
        final User user = userService.getCurrentUser();
        List<PrismScope> parentScopeIds = scopeService.getParentScopesDescending(resourceScope);
        filter = resourceListFilterService.saveOrGetByUserAndScope(user, resourceScope, filter);

        int maxRecords = LIST_PAGE_ROW_COUNT;
        Set<Integer> assignedResources = applicationContext.getBean(ResourceServiceHelperConcurrency.class).getAssignedResources(user, resourceScope,
                parentScopeIds, filter, lastSequenceIdentifier, maxRecords);
        boolean hasRedactions = actionService.hasRedactions(resourceScope, assignedResources, user);

        if (!assignedResources.isEmpty()) {
            final HashMultimap<Integer, ActionDTO> creations = actionService.getCreateResourceActions(resourceScope, assignedResources);
            List<ResourceListRowDTO> rows = resourceDAO.getResourceList(user, resourceScope, parentScopeIds, assignedResources, filter,
                    lastSequenceIdentifier, maxRecords, hasRedactions);

            applicationContext.getBean(StateServiceHelperConcurrency.class).appendSecondaryStates(resourceScope, rows, maxRecords);
            applicationContext.getBean(ActionServiceHelperConcurrency.class).appendActions(resourceScope, user, rows, creations, maxRecords);
            return rows;
        }

        return Lists.newArrayList();
    }

    public Set<Integer> getAssignedResources(User user, PrismScope scopeId, List<PrismScope> parentScopeIds) throws Exception {
        return applicationContext.getBean(ResourceServiceHelperConcurrency.class).getAssignedResources(user, scopeId, parentScopeIds, null, null, null);
    }

    public Set<Integer> getAssignedResources(User user, PrismScope scopeId, List<PrismScope> parentScopeIds, ResourceListFilterDTO filter) throws Exception {
        return applicationContext.getBean(ResourceServiceHelperConcurrency.class).getAssignedResources(user, scopeId, parentScopeIds, filter, null, null);
    }

    public List<Integer> getAssignedResources(final User user, final PrismScope scopeId, final ResourceListFilterDTO filter,
            final String lastSequenceIdentifier, final Integer recordsToRetrieve, final Junction condition) {
        return resourceDAO.getAssignedResources(user, scopeId, filter, condition, lastSequenceIdentifier, recordsToRetrieve);
    }

    public List<Integer> getAssignedResources(final User user, final PrismScope scopeId, final ResourceListFilterDTO filter,
            final String lastSequenceIdentifier, final Integer recordsToRetrieve, final Junction condition, final PrismScope parentScopeId) {
        return resourceDAO.getAssignedResources(user, scopeId, parentScopeId, filter, condition, lastSequenceIdentifier, recordsToRetrieve);
    }

    @SuppressWarnings("unchecked")
    public List<WorkflowPropertyConfigurationRepresentation> getWorkflowPropertyConfigurations(Resource<?> resource) {
        return (List<WorkflowPropertyConfigurationRepresentation>) (List<?>) customizationService.getConfigurationRepresentationsWithOrWithoutVersion(
                WORKFLOW_PROPERTY, resource, resource.getWorkflowPropertyConfigurationVersion());
    }

    public Map<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource<?> resource, PrismScope propertiesScope) throws Exception {
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource);
        Map<PrismDisplayPropertyDefinition, String> properties = Maps.newLinkedHashMap();
        for (PrismDisplayPropertyDefinition prismDisplayPropertyDefinition : PrismDisplayPropertyDefinition.getProperties(propertiesScope)) {
            properties.put(prismDisplayPropertyDefinition, loader.load(prismDisplayPropertyDefinition));
        }
        return properties;
    }

    public HashMultimap<PrismScope, Resource<?>> getUserAdministratorResources(Resource<?> resource, User user) {
        HashMultimap<PrismScope, Integer> childResources = HashMultimap.create();
        for (PrismScope childScope : scopeService.getChildScopesAscending(resource.getResourceScope())) {
            childResources.putAll(childScope, resourceDAO.getChildResources(resource, childScope));
        }

        HashMultimap<PrismScope, Resource<?>> userAdministratorResources = HashMultimap.create();
        for (UserAdministratorResourceDTO userAdministratorResource : resourceDAO.getUserAdministratorResources(resource, childResources, user)) {
            Resource<?> enclosedResource = userAdministratorResource.getResource();
            userAdministratorResources.put(enclosedResource.getResourceScope(), enclosedResource);
        }
        return userAdministratorResources;
    }

    public List<Integer> getResourcesByUserMatchingUserAndRole(PrismScope prismScope, String searchTerm, List<PrismRole> prismRoles) {
        return resourceDAO.getResourcesByMatchingUsersAndRole(prismScope, searchTerm, prismRoles);
    }

    public List<Integer> getResourcesByMatchingEnclosingResource(PrismScope enclosingResourceScope, String searchTerm) {
        return resourceDAO.getResourcesByMatchingEnclosingResources(enclosingResourceScope, searchTerm);
    }

    public ResourceStudyOption getStudyOption(ResourceOpportunity<?> resource, ImportedEntitySimple studyOption) {
        if (BooleanUtils.isTrue(resource.getAdvert().isImported())) {
            return resourceDAO.getResourceAttributeStrict(resource, ResourceStudyOption.class, "studyOption", studyOption);
        }
        return resourceDAO.getResourceAttribute(resource, ResourceStudyOption.class, "studyOption", studyOption);
    }

    public List<ImportedEntitySimple> getStudyOptions(ResourceOpportunity<?> resource) {
        if (BooleanUtils.isTrue(resource.getAdvert().isImported())) {
            List<ImportedEntitySimple> prismStudyOptions = Lists.newLinkedList();
            List<ResourceStudyOption> studyOptions = resourceDAO.getResourceAttributesStrict(resource, ResourceStudyOption.class, "studyOption", "id");
            for (ResourceStudyOption studyOption : studyOptions) {
                prismStudyOptions.add(studyOption.getStudyOption());
            }
            return prismStudyOptions;
        }

        List<ImportedEntitySimple> filteredStudyOptions = Lists.newLinkedList();
        List<ResourceStudyOption> studyOptions = resourceDAO.getResourceAttributes(resource, ResourceStudyOption.class, "studyOption", "id");

        PrismScope lastResourceScope = null;
        for (ResourceStudyOption studyOption : studyOptions) {
            PrismScope thisResourceScope = studyOption.getResource().getResourceScope();
            if (lastResourceScope != null && !thisResourceScope.equals(lastResourceScope)) {
                break;
            }
            filteredStudyOptions.add(studyOption.getStudyOption());
            lastResourceScope = thisResourceScope;
        }

        return filteredStudyOptions;
    }

    public ResourceStudyOptionInstance getFirstStudyOptionInstance(ResourceOpportunity<?> resource, ImportedEntitySimple studyOption) {
        return resourceDAO.getFirstStudyOptionInstance(resource, studyOption);
    }

    public List<String> getStudyLocations(ResourceOpportunity<?> resource) {
        List<String> filteredStudylocations = Lists.newLinkedList();
        List<ResourceStudyLocation> studyLocations = resourceDAO.getResourceAttributes(resource, ResourceStudyLocation.class, "studyLocation", null);

        PrismScope lastResourceScope = null;
        for (ResourceStudyLocation studyLocation : studyLocations) {
            PrismScope thisResourceScope = studyLocation.getResource().getResourceScope();
            if (lastResourceScope != null && !thisResourceScope.equals(lastResourceScope)) {
                break;
            }
            filteredStudylocations.add(studyLocation.getStudyLocation());
            lastResourceScope = thisResourceScope;
        }

        return filteredStudylocations;
    }

    public List<PrismActionCondition> getActionConditions(ResourceParent<?> resource) {
        List<PrismActionCondition> filteredActionConditions = Lists.newLinkedList();
        List<ResourceCondition> actionConditions = resourceDAO.getResourceConditions(resource);

        PrismScope lastResourceScope = null;
        for (ResourceCondition resourceCondition : actionConditions) {
            PrismScope thisResourceScope = resourceCondition.getResource().getResourceScope();
            if (lastResourceScope != null && !thisResourceScope.equals(lastResourceScope)) {
                break;
            }
            filteredActionConditions.add(resourceCondition.getActionCondition());
            lastResourceScope = thisResourceScope;
        }

        return filteredActionConditions;
    }

    public <T extends ResourceParent<?>, U extends ResourceParentDTO> void setResourceAttributes(T resource, U resourceDTO) {
        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunity<?> resourceOpportunity = (ResourceOpportunity<?>) resource;
            ResourceOpportunityDTO resourceOpportunityDTO = (ResourceOpportunityDTO) resourceDTO;

            Program program = resource.getProgram();
            if (!program.sameAs(resource) && BooleanUtils.isTrue(program.getAdvert().isImported())) {
                resourceOpportunity.setOpportunityType(program.getOpportunityType());
            } else {
                resourceOpportunity.setOpportunityType(importedEntityService.getByName(ImportedEntitySimple.class, resourceOpportunityDTO
                        .getOpportunityType().name()));
                List<ImportedEntitySimple> studyOptions = resourceOpportunityDTO.getStudyOptions().stream()
                        .map(studyOptionDTO -> importedEntityService.getById(ImportedEntitySimple.class, studyOptionDTO.getId()))
                        .collect(Collectors.toList());
                setStudyOptions(resourceOpportunity, studyOptions, LocalDate.now());
            }

            setStudyLocations(resourceOpportunity, resourceOpportunityDTO.getStudyLocations());
        }

        setResourceConditions(resource, resourceDTO.getConditions());
    }

    public void setResourceConditions(ResourceParent<?> resource, List<ResourceConditionDTO> resourceConditions) {
        resource.getResourceConditions().clear();
        entityService.flush();

        if (resourceConditions == null) {
            List<PrismResourceCondition> defaultResourceConditions;
            if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
                PrismOpportunityType opportunityType = PrismOpportunityType.valueOf(((ResourceOpportunity<?>) resource).getOpportunityType().getName());
                defaultResourceConditions = PrismOpportunityType.getResourceConditions(resource.getResourceScope(), opportunityType);
            } else {
                defaultResourceConditions = PrismOpportunityType.getResourceConditions(resource.getResourceScope());
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

    public void setStudyOptions(ResourceOpportunity<?> resource, List<ImportedEntitySimple> studyOptions, LocalDate baseline) {
        if (resource.getId() != null) {
            resourceDAO.disableImportedResourceStudyOptionInstances(resource);
            resourceDAO.disableImportedResourceStudyOptions(resource);
            resource.getInstanceGroups().clear();
        }

        LocalDate close = getResourceEndDate(resource);
        if (studyOptions == null) {
            List<PrismStudyOption> prismStudyOptions = PrismOpportunityType.valueOf(resource.getOpportunityType().getName()).getDefaultStudyOptions();
            studyOptions = prismStudyOptions.stream()
                    .map(prismStudyOption -> importedEntityService.getByName(ImportedEntitySimple.class, prismStudyOption.name()))
                    .collect(Collectors.toList());
        }

        List<ResourceStudyOption> resourceStudyOptions = studyOptions.stream()
                .filter(studyOption -> close == null || close.isAfter(baseline))
                .map(studyOption -> new ResourceStudyOption().withResource(resource).withStudyOption(studyOption).withApplicationStartDate(baseline)
                        .withApplicationCloseDate(close))
                .collect(Collectors.toList());
        resource.getInstanceGroups().addAll(resourceStudyOptions);
    }

    public void setStudyLocations(ResourceOpportunity<?> resource, List<String> studyLocations) {
        resource.getStudyLocations().clear();
        entityService.flush();

        if (studyLocations != null) {
            for (String studyLocation : studyLocations) {
                resource.addStudyLocation(new ResourceStudyLocation().withResource(resource).withStudyLocation(studyLocation));
            }
        }
    }

    public <T extends ResourceParentDTO> void updateResource(PrismScope resourceScope, Integer resourceId, ResourceOpportunityDTO resourceDTO) throws Exception {
        ResourceOpportunity<?> resource = (ResourceOpportunity<?>) getById(resourceScope, resourceId);
        updateResource(resource, resourceDTO);

        resource.setDurationMinimum(resourceDTO.getDurationMinimum());
        resource.setDurationMaximum(resourceDTO.getDurationMaximum());

        setStudyLocations(resource, resourceDTO.getStudyLocations());

        if (!resource.getAdvert().isImported()) {
            ImportedEntitySimple opportunityType = importedEntityService.getByName(ImportedEntitySimple.class, resourceDTO.getOpportunityType().name());
            resource.setOpportunityType(opportunityType);

            List<ImportedEntitySimple> studyOptions = resourceDTO.getStudyOptions().stream()
                    .map(studyOptionDTO -> importedEntityService.getById(ImportedEntitySimple.class, studyOptionDTO.getId()))
                    .collect(Collectors.toList());
            setStudyOptions(resource, studyOptions == null ? Lists.newArrayList() : studyOptions, new LocalDate());
        }
    }

    public <T extends ResourceParentDivision<?>, U extends ResourceParentDivisionDTO> void updateResource(T resource, U resourceDTO) {
        resource.setImportedCode(resourceDTO.getImportedCode());

        if (resourceDTO.getClass().equals(DepartmentDTO.class)) {
            departmentService.setImportedPrograms((Department) resource, ((DepartmentDTO) resourceDTO).getImportedPrograms());
        }

        updateResource(resource, (ResourceParentDTO) resourceDTO);
    }

    public <T extends ResourceParent<?>, U extends ResourceParentDTO> void updateResource(T resource, U resourceDTO) {
        AdvertDTO advertDTO = resourceDTO.getAdvert();
        Advert advert = resource.getAdvert();
        resource.setName(resourceDTO.getName());
        advertService.updateAdvert(resource.getParentResource(), advert, advertDTO, resourceDTO.getName());
        resource.setName(advert.getName());

        List<ResourceConditionDTO> resourceConditions = resourceDTO.getConditions();
        setResourceConditions(resource, resourceConditions == null ? Lists.newArrayList() : resourceConditions);
    }

    public void deleteElapsedStudyOptions() {
        LocalDate baseline = new LocalDate();
        resourceDAO.deleteElapsedStudyOptionInstances(baseline);
        resourceDAO.deleteElapsedStudyOptions(baseline);
    }

    public Junction getFilterConditions(PrismScope resourceScope, ResourceListFilterDTO filter) {
        Junction conditions = null;
        if (filter.hasConstraints()) {
            conditions = filter.getMatchMode() == ANY ? Restrictions.disjunction() : Restrictions.conjunction();
            for (ResourceListFilterConstraintDTO constraint : filter.getConstraints()) {
                resourceListConstraintBuilder.appendFilter(conditions, resourceScope, constraint);
            }
        }
        return conditions;
    }

    public LocalDate getResourceEndDate(ResourceOpportunity<?> resource) {
        return resourceDAO.getResourceEndDate(resource);
    }

    public <T extends ResourceParent<?>> Integer getActiveChildResourceCount(T resource, PrismScope childResourceScope) {
        Long count = resourceDAO.getActiveChildResourceCount(resource, childResourceScope);
        return count == null ? 0 : count.intValue();
    }

    public List<PrismStateGroup> getResourceStateGroups(Resource<?> resource) {
        return resourceDAO.getResourceStateGroups(resource);
    }

    public DateTime getLatestUpdatedTimestampSitemap(PrismScope resourceScope) {
        return resourceDAO.getLatestUpdatedTimestampSitemap(resourceScope, stateService.getActiveResourceStates(resourceScope),
                scopeService.getChildScopesWithActiveStates(resourceScope, APPLICATION));
    }

    public List<ResourceRepresentationSitemap> getResourceSitemapRepresentations(PrismScope resourceScope) {
        return resourceDAO.getResourceSitemapRepresentations(resourceScope, stateService.getActiveResourceStates(resourceScope),
                scopeService.getChildScopesWithActiveStates(resourceScope, APPLICATION));
    }

    public ResourceRepresentationRobotMetadata getResourceRobotMetadataRepresentation(Resource<?> resource, List<PrismState> scopeStates,
            HashMultimap<PrismScope, PrismState> enclosedScopes) {
        return resourceDAO.getResourceRobotMetadataRepresentation(resource, scopeStates, enclosedScopes);
    }

    public ResourceRepresentationRobotMetadataRelated getResourceRobotRelatedRepresentations(Resource<?> resource, PrismScope relatedScope, String label) {
        HashMultimap<PrismScope, PrismState> childScopes = scopeService.getChildScopesWithActiveStates(relatedScope, APPLICATION);
        List<ResourceRepresentationIdentity> childResources = resourceDAO.getResourceRobotRelatedRepresentations(resource, relatedScope,
                stateService.getActiveResourceStates(relatedScope), childScopes);
        return childResources.isEmpty() ? null : new ResourceRepresentationRobotMetadataRelated().withLabel(label).withResources(childResources);
    }

    public List<ResourceChildCreationDTO> getResourcesWhichPermitChildResourceCreation(PrismScope resourceScope, PrismScope parentScope, Integer parentId,
            PrismScope targetScope) {
        return resourceDAO.getResourcesWhichPermitChildResourceCreation(resourceScope, parentScope, parentId, targetScope, userService.isLoggedInSession());
    }

    public String generateResourceCode(Resource<?> resource) {
        return "PRiSM-" + resource.getResourceScope().getShortCode() + "-" + String.format("%010d", resource.getId());
    }

    public void reassignResource(Resource<?> resource, User newUser, String userProperty) throws Exception {
        PrismScope resourceScope = resource.getResourceScope();
        if (userService.mergeUserAssignment(resource, newUser, userProperty)) {
            Set<String> commentUserProperties = userService.getUserProperties(Comment.class);
            Set<String> commentAssignedUserUserProperties = userService.getUserProperties(CommentAssignedUser.class);
            Set<String> documentUserProperties = userService.getUserProperties(Document.class);

            for (Comment oldComment : commentService.getResourceOwnerComments(resource)) {
                for (String commentUserProperty : commentUserProperties) {
                    userService.mergeUserAssignment(oldComment, newUser, commentUserProperty);
                }
            }

            for (CommentAssignedUser oldCommentAssignedUser : commentService.getResourceOwnerCommentAssignedUsers(resource)) {
                for (String commentAssignedUserUserProperty : commentAssignedUserUserProperties) {
                    userService.mergeUserAssignment(oldCommentAssignedUser, newUser, commentAssignedUserUserProperty);
                }
            }

            for (Document oldDocument : documentService.getResourceOwnerDocuments(resource)) {
                for (String documentUserProperty : documentUserProperties) {
                    userService.mergeUserAssignment(oldDocument, newUser, documentUserProperty);
                }
            }
        } else if (!resourceScope.equals(SYSTEM)) {
            Action action = actionService.getById(PrismAction.valueOf(resourceScope.name() + "_TERMINATE"));
            actionService.executeAction(resource, action, new Comment().withUser(systemService.getSystem().getUser()) //
                    .withAction(action).withDeclinedResponse(false).withCreatedTimestamp(new DateTime()));
        } else {
            throw new WorkflowEngineException("Cannot terminate system resource");
        }
    }

    private void createOrUpdateStateTransitionSummary(Resource<?> resource, DateTime baselineTime) {
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

    private <T extends ResourceStateDefinition, U extends CommentStateDefinition> void deleteResourceStates(
            Set<T> resourceStateDefinitions, Set<U> commentStateDefinitions) {
        List<State> preservedStates = commentStateDefinitions.stream().map(CommentStateDefinition::getState).collect(Collectors.toList());

        resourceStateDefinitions.stream()
                .filter(resourceState -> !preservedStates.contains(resourceState.getState()))
                .forEach(entityService::delete);
        resourceStateDefinitions.clear();
    }

    private <T extends ResourceStateDefinition, U extends CommentStateDefinition> void insertResourceStates(
            Resource<?> resource, Set<T> resourceStateDefinitions, Set<U> commentStateDefinitions, Class<T> resourceStateClass, LocalDate baseline)
            throws Exception {
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
            } else {
                persistentResourceStateDefinition.setPrimaryState(commentState.getPrimaryState());
            }
        }
    }

    private <T extends Resource<?>> void setResourceUpdated(T resource, DateTime baseline) {
        resource.setUpdatedTimestamp(baseline);
        resource.setSequenceIdentifier(Long.toString(baseline.getMillis()) + String.format("%010d", resource.getId()));
    }

}
