package com.zuehlke.pgadmissions.services;

import static com.google.common.collect.Lists.newArrayList;
import static com.zuehlke.pgadmissions.PrismConstants.RATING_PRECISION;
import static com.zuehlke.pgadmissions.PrismConstants.RESOURCE_LIST_PAGE_ROW_COUNT;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getResourceOpportunityCategoryProjection;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.WORKFLOW_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismFilterMatchMode.ANY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption.FULL_TIME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.getUnverifiedViewerRole;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeSectionDefinition.getRequiredSections;
import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.toBoolean;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
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
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeSectionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
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
import com.zuehlke.pgadmissions.dto.ResourceOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceListRowDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceRatingSummaryDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceStandardDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceTargetDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceTargetListDTO;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceConditionDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDivisionDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationIdentity;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobotMetadata;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobotMetadataRelated;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSitemap;
import com.zuehlke.pgadmissions.services.builders.PrismResourceListConstraintBuilder;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceCompletenessEvaluator;
import com.zuehlke.pgadmissions.workflow.executors.action.ActionExecutor;
import com.zuehlke.pgadmissions.workflow.resolvers.state.duration.StateDurationResolver;
import com.zuehlke.pgadmissions.workflow.transition.creators.ResourceCreator;
import com.zuehlke.pgadmissions.workflow.transition.populators.ResourcePopulator;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

import jersey.repackaged.com.google.common.collect.Sets;

@Service
@Transactional
public class ResourceService {

    @Value("${system.id}")
    private Integer systemId;

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
    private ImportedEntityService importedEntityService;

    @Inject
    private EntityService entityService;

    @Inject
    private NotificationService notificationService;

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
    private InstitutionService institutionService;

    @Inject
    private ResourceListFilterService resourceListFilterService;

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

    @SuppressWarnings("unchecked")
    public <T extends ResourceCreationDTO> ActionOutcomeDTO createResource(User user, Action action, T resourceDTO) {
        PrismScope creationScope = action.getCreationScope().getId();

        ResourceCreator<T> resourceCreator = (ResourceCreator<T>) applicationContext.getBean(creationScope.getResourceCreator());
        Resource resource = resourceCreator.create(user, resourceDTO);

        resource.setWorkflowPropertyConfigurationVersion(resourceDTO.getWorkflowPropertyConfigurationVersion());

        Comment comment = new Comment().withResource(resource).withUser(user).withAction(action).withDeclinedResponse(false).withCreatedTimestamp(new DateTime())
                .addAssignedUser(user, roleService.getCreatorRole(resource), CREATE);

        return actionService.executeUserAction(resource, action, comment);
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> void persistResource(T resource, Comment comment) {
        DateTime baseline = new DateTime();
        if (comment.isCreateComment()) {
            resource.setCreatedTimestamp(baseline);
            resource.setUpdatedTimestamp(baseline);

            if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
                resource.getAdvert().setResource(resource);
                ((ResourceParent) (resource)).setUpdatedTimestampSitemap(baseline);
            }

            entityService.save(resource);
            entityService.flush();

            resource.setSequenceIdentifier(Long.toString(baseline.getMillis()) + String.format("%010d", resource.getId()));

            Integer workflowPropertyConfigurationVersion = resource.getWorkflowPropertyConfigurationVersion();
            if (workflowPropertyConfigurationVersion == null) {
                workflowPropertyConfigurationVersion = customizationService.getActiveConfigurationVersion(WORKFLOW_PROPERTY, resource);
            }
            resource.setWorkflowPropertyConfigurationVersion(workflowPropertyConfigurationVersion);

            Class<? extends ResourcePopulator<T>> populator = (Class<? extends ResourcePopulator<T>>) resource.getResourceScope().getResourcePopulator();
            if (populator != null) {
                applicationContext.getBean(populator).populate(resource);
            }

            resource.setCode(generateResourceCode(resource));
            entityService.flush();
        } else if (comment.isUserComment() || resource.getSequenceIdentifier() == null) {
            resource.setUpdatedTimestamp(baseline);
            resource.setSequenceIdentifier(Long.toString(baseline.getMillis()) + String.format("%010d", resource.getId()));
            entityService.flush();
        }
    }

    public <T extends ResourceCreationDTO> ActionOutcomeDTO executeAction(User user, CommentDTO commentDTO) {
        return executeAction(user, commentDTO, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends ResourceCreationDTO> ActionOutcomeDTO executeAction(User user, CommentDTO commentDTO, String referralEmailAddress) {
        Resource operativeResource = null;
        ActionOutcomeDTO actionOutcome = null;
        if (commentDTO.getAction().getActionCategory().equals(CREATE_RESOURCE)) {
            T resourceDTO = (T) commentDTO.getResource();
            Action action = actionService.getById(commentDTO.getAction());
            resourceDTO.setParentResource(commentDTO.getResource().getParentResource());
            actionOutcome = createResource(user, action, resourceDTO);
            operativeResource = actionOutcome.getResource().getParentResource();
        } else if (commentDTO.isRequestUserAction()) {
            operativeResource = getById(commentDTO.getResource().getScope(), commentDTO.getResource().getId());
            userService.requestUser(commentDTO.getAssignedUsers().get(0).getUser(), operativeResource, getUnverifiedViewerRole(operativeResource));
            actionOutcome = new ActionOutcomeDTO().withTransitionAction(actionService.getViewEditAction(operativeResource)).withTransitionResource(operativeResource);
        } else {
            Class<? extends ActionExecutor> actionExecutor = commentDTO.getAction().getScope().getActionExecutor();
            if (actionExecutor != null) {
                actionOutcome = applicationContext.getBean(actionExecutor).execute(commentDTO);
                operativeResource = actionOutcome.getResource();
            }
        }

        return actionOutcome;
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> void preProcessResource(T resource, Comment comment) {
        Class<? extends ResourceProcessor<T>> processor = (Class<? extends ResourceProcessor<T>>) resource
                .getResourceScope().getResourcePreprocessor();
        if (processor != null) {
            applicationContext.getBean(processor).process(resource, comment);
            entityService.flush();
        }
    }

    public void recordStateTransition(Resource resource, Comment comment, State state, State transitionState) {
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
    public <T extends Resource> void processResource(T resource, Comment comment) {
        Class<? extends ResourceProcessor<T>> processor = (Class<? extends ResourceProcessor<T>>) resource.getResourceScope().getResourceProcessor();
        if (processor != null) {
            applicationContext.getBean(processor).process(resource, comment);
        }

        StateDurationDefinition stateDurationDefinition = resource.getState().getStateDurationDefinition();
        if (comment.isStateTransitionComment()
                || (stateDurationDefinition != null && BooleanUtils.isTrue(stateDurationDefinition.getEscalation()))) {
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
            resource.setDueDate(baseline
                    .plusDays(stateDurationConfiguration == null ? 0 : stateDurationConfiguration.getDuration()));
        }
        entityService.flush();
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> void postProcessResource(T resource, Comment comment) {
        Class<? extends ResourceProcessor<T>> processor = (Class<? extends ResourceProcessor<T>>) resource
                .getResourceScope().getResourcePostprocessor();
        if (processor != null) {
            applicationContext.getBean(processor).process(resource, comment);
        }

        if (comment.isUserCreationComment()) {
            notificationService.resetNotifications(resource);
        }

        if (comment.isStateGroupTransitionComment() && comment.getAction().getCreationScope() == null) {
            createOrUpdateStateTransitionSummary(resource, new DateTime());
        }

        if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
            setResourceAdvertIncompleteSection((ResourceParent) resource);
        }
        entityService.flush();
    }

    public Comment executeUpdate(Resource resource, PrismDisplayPropertyDefinition messageIndex, CommentAssignedUser... assignees) {
        User user = userService.getCurrentUser();
        Action action = actionService.getViewEditAction(resource);

        Comment comment = new Comment().withUser(user).withAction(action)
                .withContent(applicationContext.getBean(PropertyLoader.class).localizeLazy(resource).loadLazy(messageIndex))
                .withDeclinedResponse(false).withCreatedTimestamp(new DateTime());

        for (CommentAssignedUser assignee : assignees) {
            comment.addAssignedUser(assignee.getUser(), assignee.getRole(), assignee.getRoleTransitionType());
            entityService.evict(assignee);
        }

        actionService.executeUserAction(resource, action, comment);
        return comment;
    }

    public <T extends Resource> T getOperativeResource(T resource, Action action) {
        return action.getActionCategory().equals(CREATE_RESOURCE) ? resource.getParentResource() : resource;
    }

    public List<Integer> getResourcesToEscalate(PrismScope resourceScope, PrismAction actionId, LocalDate baseline) {
        return resourceDAO.getResourcesToEscalate(resourceScope, actionId, baseline);
    }

    public List<Integer> getResourcesToPropagate(PrismScope propagatingScope, Integer propagatingId, PrismScope propagatedScope, PrismAction actionId) {
        return resourceDAO.getResourcesToPropagate(propagatingScope, propagatingId, propagatedScope, actionId);
    }

    @SuppressWarnings("unchecked")
    public List<ResourceListRowDTO> getResourceList(User user, PrismScope scope, List<PrismScope> parentScopes, ResourceListFilterDTO filter, String sequenceId,
            Collection<Integer> resourceIds, Collection<Integer> onlyAsPartnerResourceIds) throws Exception {
        if (!resourceIds.isEmpty()) {
            boolean hasRedactions = actionService.hasRedactions(user, scope);
            List<ResourceListRowDTO> rows = resourceDAO.getResourceList(user, scope, parentScopes, resourceIds, filter, sequenceId, RESOURCE_LIST_PAGE_ROW_COUNT, hasRedactions);

            Map<Integer, ResourceListRowDTO> rowIndex = rows.stream().collect(Collectors.toMap(row -> (row.getResourceId()), row -> (row)));
            Set<Integer> filteredResourceIds = rowIndex.keySet();

            LinkedHashMultimap<Integer, PrismState> secondaryStates = stateService.getSecondaryResourceStates(scope, filteredResourceIds);
            LinkedHashMultimap<Integer, ActionDTO> permittedActions = actionService.getPermittedActions(scope, filteredResourceIds, user);

            Collection<Integer> filteredNativeOwnerResourceIds = ListUtils.removeAll(filteredResourceIds, onlyAsPartnerResourceIds);
            LinkedHashMultimap<Integer, ActionDTO> creationActions = actionService.getCreateResourceActions(scope, filteredNativeOwnerResourceIds, APPLICATION);

            rowIndex.keySet().forEach(resourceId -> {
                ResourceListRowDTO row = rowIndex.get(resourceId);
                row.setSecondaryStateIds(Lists.newLinkedList(secondaryStates.get(resourceId)));

                List<ActionDTO> actions = Lists.newLinkedList(permittedActions.get(resourceId));
                actions.addAll(creationActions.get(resourceId));
                row.setActions(actions);
            });
            return rows;
        }

        return Lists.newArrayList();
    }

    @SuppressWarnings("unchecked")
    public List<WorkflowPropertyConfigurationRepresentation> getWorkflowPropertyConfigurations(Resource resource) {
        return (List<WorkflowPropertyConfigurationRepresentation>) (List<?>) customizationService.getConfigurationRepresentationsWithOrWithoutVersion(WORKFLOW_PROPERTY, resource,
                resource.getWorkflowPropertyConfigurationVersion());
    }

    public Map<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource resource, PrismScope propertiesScope) {
        Map<PrismDisplayPropertyDefinition, String> properties = Maps.newLinkedHashMap();
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localizeLazy(resource);
        for (PrismDisplayPropertyDefinition prismDisplayPropertyDefinition : PrismDisplayPropertyDefinition.getProperties(propertiesScope)) {
            properties.put(prismDisplayPropertyDefinition, loader.loadEager(prismDisplayPropertyDefinition));
        }
        return properties;
    }

    public HashMultimap<PrismScope, Integer> getUserAdministratorResources(User user) {
        HashMultimap<PrismScope, Integer> resources = HashMultimap.create();
        for (PrismScope scope : scopeService.getParentScopesDescending(APPLICATION, SYSTEM)) {
            for (ResourceStandardDTO resource : resourceDAO.getUserAdministratorResources(scope, user)) {
                resources.put(resource.getScope(), resource.getId());
            }
        }
        return resources;
    }

    public List<Resource> getResourcesByUser(PrismScope prismScope, User user) {
        return resourceDAO.getResourcesByUser(prismScope, user);
    }

    public List<Integer> getResourcesByMatchingUserAndRole(PrismScope prismScope, String searchTerm, List<PrismRole> prismRoles) {
        return resourceDAO.getResourcesByMatchingUsersAndRole(prismScope, searchTerm, prismRoles);
    }

    public List<Integer> getResourcesByMatchingEnclosingResource(PrismScope enclosingResourceScope, String searchTerm) {
        return resourceDAO.getResourcesByMatchingEnclosingResources(enclosingResourceScope, searchTerm);
    }

    public ResourceStudyOption getStudyOption(ResourceParent resource, ImportedEntitySimple studyOption) {
        if (BooleanUtils.isTrue(resource.getAdvert().isImported())) {
            return resourceDAO.getResourceAttributeStrict(resource, ResourceStudyOption.class, "studyOption", studyOption);
        }
        return resourceDAO.getResourceAttribute(resource, ResourceStudyOption.class, "studyOption", studyOption);
    }

    public List<ImportedEntitySimple> getStudyOptions(ResourceOpportunity resource) {
        if (BooleanUtils.isTrue(resource.getAdvert().isImported())) {
            List<ImportedEntitySimple> prismStudyOptions = Lists.newLinkedList();
            List<ResourceStudyOption> studyOptions = resourceDAO.getResourceAttributesStrict(resource, ResourceStudyOption.class, "studyOption", "id");
            for (ResourceStudyOption studyOption : studyOptions) {
                prismStudyOptions.add(studyOption.getStudyOption());
            }
            return prismStudyOptions;
        }

        List<ImportedEntitySimple> filteredStudyOptions = Lists.newLinkedList();
        List<ResourceStudyOption> studyOptions = resourceDAO.getResourceAttributes(resource, ResourceStudyOption.class,
                "studyOption", "id");

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

    public ResourceStudyOptionInstance getFirstStudyOptionInstance(ResourceOpportunity resource, ImportedEntitySimple studyOption) {
        return resourceDAO.getFirstStudyOptionInstance(resource, studyOption);
    }

    public List<String> getStudyLocations(ResourceOpportunity resource) {
        List<String> filteredStudyLocations = Lists.newLinkedList();
        List<ResourceStudyLocation> studyLocations = resourceDAO.getResourceAttributes(resource,
                ResourceStudyLocation.class, "studyLocation", null);

        PrismScope lastResourceScope = null;
        for (ResourceStudyLocation studyLocation : studyLocations) {
            PrismScope thisResourceScope = studyLocation.getResource().getResourceScope();
            if (lastResourceScope != null && !thisResourceScope.equals(lastResourceScope)) {
                break;
            }
            filteredStudyLocations.add(studyLocation.getStudyLocation());
            lastResourceScope = thisResourceScope;
        }

        return filteredStudyLocations;
    }

    public List<PrismActionCondition> getActionConditions(ResourceParent resource) {
        List<PrismActionCondition> filteredActionConditions = Lists.newLinkedList();
        resourceDAO.getResourceConditions(resource).forEach(condition -> {
            filteredActionConditions.add(condition.getActionCondition());
        });
        return filteredActionConditions;
    }

    public <T extends ResourceParent, U extends ResourceParentDTO> void setResourceAttributes(T resource, U resourceDTO) {
        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunity resourceOpportunity = (ResourceOpportunity) resource;
            ResourceOpportunityDTO resourceOpportunityDTO = (ResourceOpportunityDTO) resourceDTO;

            Program program = resource.getProgram();
            if (program != null && !resource.sameAs(program) && BooleanUtils.isTrue(program.getAdvert().isImported())) {
                ImportedEntitySimple opportunityType = program.getOpportunityType();
                setResourceOpportunityType(resourceOpportunity, opportunityType);
            } else {
                ImportedEntitySimple opportunityType = importedEntityService.getByName(ImportedEntitySimple.class, resourceOpportunityDTO.getOpportunityType().name());
                setResourceOpportunityType(resourceOpportunity, opportunityType);
                List<ImportedEntitySimple> studyOptions = resourceOpportunityDTO.getStudyOptions().stream()
                        .map(studyOptionDTO -> importedEntityService.getById(ImportedEntitySimple.class, studyOptionDTO.getId())).collect(Collectors.toList());
                setStudyOptions(resourceOpportunity, studyOptions, LocalDate.now());
            }

            setStudyLocations(resourceOpportunity, resourceOpportunityDTO.getStudyLocations());
        }

        setResourceConditions(resource, resourceDTO.getConditions());
    }

    public void setResourceConditions(ResourceParent resource, List<ResourceConditionDTO> resourceConditions) {
        resource.getResourceConditions().clear();
        entityService.flush();

        if (resourceConditions == null) {
            resourceConditions = Lists.newArrayList();
            switch (resource.getResourceScope()) {
            case INSTITUTION:
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_DEPARTMENT).withInternalMode(false).withExternalMode(true));
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_PROJECT).withInternalMode(true).withExternalMode(true));
                break;
            case DEPARTMENT:
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_PROJECT).withInternalMode(true).withExternalMode(true));
                break;
            case PROGRAM:
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_PROJECT).withInternalMode(true).withExternalMode(false));
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_APPLICATION).withInternalMode(true).withExternalMode(false));
                break;
            case PROJECT:
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_APPLICATION).withInternalMode(true).withExternalMode(false));
                break;
            default:
                throw new UnsupportedOperationException("Resource type does not have action conditions");
            }
        }

        resourceConditions.forEach(condition -> {
            boolean internal = toBoolean(condition.getInternalMode());
            boolean external = toBoolean(condition.getExternalMode());

            if (internal || external) {
                resource.addResourceCondition(
                        new ResourceCondition().withResource(resource).withActionCondition(condition.getActionCondition()).withInternalMode(internal).withExternalMode(external));
            }
        });
    }

    public void setStudyOptions(ResourceOpportunity resource, List<ImportedEntitySimple> studyOptions, LocalDate baseline) {
        if (resource.getId() != null) {
            resourceDAO.disableImportedResourceStudyOptionInstances(resource);
            resourceDAO.disableImportedResourceStudyOptions(resource);
            resource.getInstanceGroups().clear();
        }

        LocalDate close = getResourceEndDate(resource);
        if (studyOptions == null) {
            studyOptions = studyOptions == null ? newArrayList(importedEntityService.getByName(ImportedEntitySimple.class, FULL_TIME.name())) : studyOptions;
        }

        resource.getInstanceGroups().addAll(studyOptions.stream().filter(studyOption -> close == null || close.isAfter(baseline)).map(
                studyOption -> new ResourceStudyOption().withResource(resource).withStudyOption(studyOption).withApplicationStartDate(baseline).withApplicationCloseDate(close))
                .collect(Collectors.toList()));
    }

    public void setStudyLocations(ResourceOpportunity resource, List<String> studyLocations) {
        resource.getStudyLocations().clear();
        entityService.flush();

        if (studyLocations != null) {
            for (String studyLocation : studyLocations) {
                resource.addStudyLocation(
                        new ResourceStudyLocation().withResource(resource).withStudyLocation(studyLocation));
            }
        }
    }

    public <T extends ResourceParentDTO> void updateResource(PrismScope resourceScope, Integer resourceId, ResourceOpportunityDTO resourceDTO) {
        ResourceOpportunity resource = (ResourceOpportunity) getById(resourceScope, resourceId);
        updateResource(resource, resourceDTO);

        resource.setDurationMinimum(resourceDTO.getDurationMinimum());
        resource.setDurationMaximum(resourceDTO.getDurationMaximum());

        setStudyLocations(resource, resourceDTO.getStudyLocations());

        if (!resource.getAdvert().isImported()) {
            ImportedEntitySimple opportunityType = importedEntityService.getByName(ImportedEntitySimple.class,
                    resourceDTO.getOpportunityType().name());
            resource.setOpportunityType(opportunityType);

            List<ImportedEntitySimple> studyOptions = resourceDTO.getStudyOptions().stream().map(
                    studyOptionDTO -> importedEntityService.getById(ImportedEntitySimple.class, studyOptionDTO.getId()))
                    .collect(Collectors.toList());
            setStudyOptions(resource, studyOptions == null ? Lists.newArrayList() : studyOptions, new LocalDate());
        }
    }

    public <T extends ResourceParentDivision, U extends ResourceParentDivisionDTO> void updateResource(T resource, U resourceDTO) {
        resource.setImportedCode(resourceDTO.getImportedCode());
        updateResource(resource, (ResourceParentDTO) resourceDTO);
    }

    public <T extends ResourceParent, U extends ResourceParentDTO> void updateResource(T resource, U resourceDTO) {
        AdvertDTO advertDTO = resourceDTO.getAdvert();

        String name = resourceDTO.getName();
        Advert advert = resource.getAdvert();
        resource.setName(name);
        advert.setName(name);

        advertService.updateAdvert(resource.getParentResource(), advert, advertDTO, resourceDTO.getName());

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

    public LocalDate getResourceEndDate(ResourceOpportunity resource) {
        return resourceDAO.getResourceEndDate(resource);
    }

    public <T extends ResourceParent> Integer getActiveChildResourceCount(T resource, PrismScope childResourceScope) {
        Long count = resourceDAO.getActiveChildResourceCount(resource, childResourceScope);
        return count == null ? 0 : count.intValue();
    }

    public List<PrismStateGroup> getResourceStateGroups(Resource resource) {
        return resourceDAO.getResourceStateGroups(resource);
    }

    public DateTime getLatestUpdatedTimestampSitemap(PrismScope resourceScope) {
        return resourceDAO.getLatestUpdatedTimestampSitemap(resourceScope,
                stateService.getActiveResourceStates(resourceScope),
                scopeService.getChildScopesWithActiveStates(resourceScope, PROJECT));
    }

    public List<ResourceRepresentationSitemap> getResourceSitemapRepresentations(PrismScope resourceScope) {
        return resourceDAO.getResourceSitemapRepresentations(resourceScope,
                stateService.getActiveResourceStates(resourceScope),
                scopeService.getChildScopesWithActiveStates(resourceScope, PROJECT));
    }

    public ResourceRepresentationRobotMetadata getResourceRobotMetadataRepresentation(Resource resource,
            List<PrismState> scopeStates, HashMultimap<PrismScope, PrismState> enclosedScopes) {
        return resourceDAO.getResourceRobotMetadataRepresentation(resource, scopeStates, enclosedScopes);
    }

    public ResourceRepresentationRobotMetadataRelated getResourceRobotRelatedRepresentations(Resource resource,
            PrismScope relatedScope, String label) {
        HashMultimap<PrismScope, PrismState> childScopes = scopeService.getChildScopesWithActiveStates(relatedScope, PROJECT);
        List<ResourceRepresentationIdentity> childResources = resourceDAO.getResourceRobotRelatedRepresentations(
                resource, relatedScope, stateService.getActiveResourceStates(relatedScope), childScopes);
        return childResources.isEmpty() ? null
                : new ResourceRepresentationRobotMetadataRelated().withLabel(label).withResources(childResources);
    }

    public Set<ResourceTargetDTO> getResourceTargets(Advert advert, List<Integer> institutions, List<Integer> departments) {
        PrismScope[] institutionScopes = new PrismScope[] { INSTITUTION };
        List<PrismState> institutionStates = stateService.getActiveResourceStates(INSTITUTION);
        List<Integer> targetInstitutions = advertService.getAdvertTargetResources(advert, INSTITUTION, true);

        ResourceTargetListDTO targets = new ResourceTargetListDTO(advert);
        if (isNotEmpty(institutions)) {
            addResourceTargets(targets, resourceDAO.getResourceTargets(institutionScopes, institutions, institutionStates), targetInstitutions);
        }

        boolean hasDepartments = isNotEmpty(departments);
        if (hasDepartments) {
            List<Integer> departmentInstitutions = institutionService.getInstitutionsByDepartments(departments, institutionStates);
            addResourceTargets(targets, resourceDAO.getResourceTargets(institutionScopes, departmentInstitutions, institutionStates), targetInstitutions);

            List<PrismState> departmentStates = stateService.getActiveResourceStates(DEPARTMENT);
            List<Integer> targetDepartments = advertService.getAdvertTargetResources(advert, DEPARTMENT, true);
            addResourceTargets(targets, resourceDAO.getResourceTargets(new PrismScope[] { DEPARTMENT, INSTITUTION }, departments, departmentStates), targetDepartments);
        }

        return targets.keySet();
    }

    public ResourceStandardDTO getResourceWithParents(Resource resource, List<PrismScope> parentScopes) {
        PrismScope resourceScope = resource.getResourceScope();
        if (!resourceScope.equals(SYSTEM)) {
            return resourceDAO.getParentResources(SYSTEM, systemId, resourceScope, resource.getId(), parentScopes);
        }
        return null;
    }

    public List<ResourceTargetDTO> getResourcesWhichPermitTargeting(PrismScope resourceScope, String searchTerm) {
        return resourceDAO.getResourcesWhichPermitTargeting(SYSTEM, systemId, resourceScope,
                scopeService.getParentScopesDescending(resourceScope, INSTITUTION), searchTerm);
    }

    public List<ResourceChildCreationDTO> getResourcesWhichPermitChildResourceCreation(PrismScope filterScope, Integer filterResourceId, PrismScope resourceScope,
            PrismScope creationScope, String searchTerm) {
        return resourceDAO.getResourcesWhichPermitChildResourceCreation(filterScope, filterResourceId, resourceScope,
                scopeService.getParentScopesDescending(resourceScope, filterScope), creationScope, searchTerm, userService.isUserLoggedIn());
    }

    public String generateResourceCode(Resource resource) {
        return "PRiSM-" + resource.getResourceScope().getShortCode() + "-" + String.format("%010d", resource.getId());
    }

    public void reassignResource(Resource resource, User newUser, String userProperty) {
        PrismScope resourceScope = resource.getResourceScope();
        if (userService.mergeUserAssignment(resource, newUser, userProperty)) {
            Set<String> commentUserProperties = userService.getUserProperties(Comment.class);
            Set<String> commentAssignedUserUserProperties = userService.getUserProperties(CommentAssignedUser.class);
            Set<String> documentUserProperties = userService.getUserProperties(Document.class);

            userService.mergeUserAssignment(resource.getAdvert(), newUser, userProperty);

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

    @SuppressWarnings("unchecked")
    public <T extends ResourceParent> void setResourceAdvertIncompleteSection(T resource) {
        List<PrismScopeSectionDefinition> incompleteSections = Lists.newLinkedList();
        for (PrismScopeSectionDefinition section : getRequiredSections(resource.getResourceScope())) {
            ResourceCompletenessEvaluator<T> completenessEvaluator = (ResourceCompletenessEvaluator<T>) applicationContext.getBean(section.getCompletenessEvaluator());
            if (!completenessEvaluator.evaluate(resource)) {
                incompleteSections.add(section);
            }
        }

        resource.setAdvertIncompleteSection(Joiner.on("|").join(incompleteSections));
    }

    public ResourceParent getActiveResourceByName(PrismScope resourceScope, String name) {
        Class<? extends Resource> resourceClass = resourceScope.getResourceClass();
        if (ResourceParent.class.isAssignableFrom(resourceClass)) {
            return resourceDAO.getActiveResourceByName(resourceScope, name, stateService.getActiveResourceStates(resourceScope));
        }
        return null;
    }

    public <T extends ResourceParent> void synchronizeResourceEndorsement(T resource, Comment comment) {
        ResourceRatingSummaryDTO ratingSummary = resourceDAO.getResourceRatingSummary(resource);
        resource.setOpportunityRatingCount(ratingSummary.getRatingCount().intValue());
        resource.setOpportunityRatingAverage(BigDecimal.valueOf(ratingSummary.getRatingAverage()));

        entityService.flush();

        scopeService.getParentScopesDescending(resource.getResourceScope(), INSTITUTION).forEach(scope -> {
            ResourceParent parent = (ResourceParent) resource.getEnclosingResource(scope);
            ResourceRatingSummaryDTO parentRatingSummary = resourceDAO.getResourceRatingSummary(resource, parent);
            parent.setOpportunityRatingCount(parentRatingSummary.getRatingCount().intValue());
            parent.setOpportunityRatingAverage(BigDecimal.valueOf(parentRatingSummary.getRatingAverage()).setScale(RATING_PRECISION, HALF_UP));
        });
    }

    public Set<ResourceOpportunityCategoryDTO> getResources(User user, PrismScope scope, List<PrismScope> parentScopes) {
        return getResources(user, scope, parentScopes, null, null);
    }

    public Set<ResourceOpportunityCategoryDTO> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, ResourceListFilterDTO filter) {
        filter = resourceListFilterService.saveOrGetByUserAndScope(user, scope, filter);
        return getResources(user, scope, parentScopes, filter, getFilterConditions(scope, filter));
    }

    public <T> Set<T> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, ResourceListFilterDTO filter, ProjectionList columns, Class<T> resourceClass) {
        return getResources(user, scope, parentScopes, filter, columns, getFilterConditions(scope, filter), resourceClass);
    }

    public void setOpportunityCategories(ResourceParent parent, String opportunityCategories) {
        parent.setOpportunityCategories(opportunityCategories);
        parent.getAdvert().setOpportunityCategories(opportunityCategories);
    }

    private Set<ResourceOpportunityCategoryDTO> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, ResourceListFilterDTO filter, Junction condition) {
        return getResources(user, scope, parentScopes, filter, getResourceOpportunityCategoryProjection(), condition, ResourceOpportunityCategoryDTO.class);
    }

    private <T> Set<T> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, ResourceListFilterDTO filter, ProjectionList columns,
            Junction conditions, Class<T> responseClass) {
        Set<T> resources = Sets.newHashSet();
        Boolean onlyAsPartner = responseClass.equals(ResourceOpportunityCategoryDTO.class) ? false : null;
        addResources(resourceDAO.getResources(user, scope, filter, columns, conditions, responseClass), resources, onlyAsPartner);

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScopeId : parentScopes) {
                addResources(resourceDAO.getResources(user, scope, parentScopeId, filter, columns, conditions, responseClass), resources, onlyAsPartner);
            }

            onlyAsPartner = onlyAsPartner == null ? null : true;
            for (PrismScope partnerScopeId : new PrismScope[] { DEPARTMENT, INSTITUTION }) {
                addResources(resourceDAO.getPartnerResources(user, scope, partnerScopeId, filter, columns, conditions, responseClass), resources, onlyAsPartner);
            }
        }

        return resources;
    }

    private <T> void addResources(List<T> resources, Set<T> resourcesFiltered, Boolean onlyAsPartner) {
        boolean processOnlyAsPartner = onlyAsPartner != null;
        resources.forEach(resource -> {
            resourcesFiltered.add(resource);
            if (processOnlyAsPartner) {
                ((ResourceOpportunityCategoryDTO) resource).setOnlyAsPartner(onlyAsPartner);
            }
        });
    }

    private void createOrUpdateStateTransitionSummary(Resource resource, DateTime baselineTime) {
        String transitionStateSelection = Joiner.on("|").join(stateService.getCurrentStates(resource));

        ResourceStateTransitionSummary transientTransitionSummary = new ResourceStateTransitionSummary()
                .withResource(resource.getParentResource()).withStateGroup(resource.getPreviousState().getStateGroup())
                .withTransitionStateSelection(transitionStateSelection).withFrequency(1)
                .withUpdatedTimestamp(baselineTime);
        ResourceStateTransitionSummary persistentTransitionSummary = entityService
                .getDuplicateEntity(transientTransitionSummary);

        if (persistentTransitionSummary == null) {
            entityService.save(transientTransitionSummary);
        } else {
            persistentTransitionSummary.setFrequency(persistentTransitionSummary.getFrequency() + 1);
            persistentTransitionSummary.setUpdatedTimestamp(baselineTime);
        }
    }

    private <T extends ResourceStateDefinition, U extends CommentStateDefinition> void deleteResourceStates(
            Set<T> resourceStateDefinitions, Set<U> commentStateDefinitions) {
        List<State> preservedStates = commentStateDefinitions.stream().map(CommentStateDefinition::getState)
                .collect(Collectors.toList());

        resourceStateDefinitions.stream().filter(resourceState -> !preservedStates.contains(resourceState.getState()))
                .forEach(entityService::delete);
        resourceStateDefinitions.clear();
    }

    private <T extends ResourceStateDefinition, U extends CommentStateDefinition> void insertResourceStates(
            Resource resource, Set<T> resourceStateDefinitions, Set<U> commentStateDefinitions,
            Class<T> resourceStateClass, LocalDate baseline) {
        for (U commentState : commentStateDefinitions) {
            T transientResourceStateDefinition;
            try {
                transientResourceStateDefinition = resourceStateClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new Error(e);
            }
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

    private void addResourceTargets(ResourceTargetListDTO targets, List<ResourceTargetDTO> newTargets, List<Integer> targetResources) {
        newTargets.forEach(newTarget -> {
            addResourceTarget(targets, newTarget, targetResources);
        });
    }

    private void addResourceTarget(ResourceTargetListDTO targets, ResourceTargetDTO target, List<Integer> targetResources) {
        target.setSelected(targetResources.contains(target.getId()));
        targets.add(target);
    }

    private void setResourceOpportunityType(ResourceOpportunity resourceOpportunity, ImportedEntitySimple opportunityType) {
        resourceOpportunity.setOpportunityType(opportunityType);
        resourceOpportunity.getAdvert().setOpportunityType(opportunityType);

        PrismOpportunityCategory opportunityCategory = PrismOpportunityType.valueOf(opportunityType.getName()).getCategory();
        resourceOpportunity.setOpportunityCategories(opportunityCategory.name());

        for (PrismScope scope : new PrismScope[] { DEPARTMENT, INSTITUTION }) {
            ResourceParent parent = (ResourceParent) resourceOpportunity.getEnclosingResource(scope);
            if (parent != null) {
                String opportunityCategories = parent.getOpportunityCategories();
                if (opportunityCategories == null) {
                    opportunityCategories = opportunityCategory.name();
                    setOpportunityCategories(parent, opportunityCategories);
                } else {
                    Set<String> opportunityCategoriesSplit = Sets.newHashSet(opportunityCategories.split("\\|"));
                    opportunityCategoriesSplit.add(opportunityCategories);

                    opportunityCategories = Joiner.on("|").join(opportunityCategoriesSplit);
                    setOpportunityCategories(parent, opportunityCategories);
                }
            }
        }
    }

}
