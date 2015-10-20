package com.zuehlke.pgadmissions.services;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.targetScopes;
import static com.zuehlke.pgadmissions.domain.definitions.PrismFilterMatchMode.ANY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext.APPLICANT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismRoleContext.STUDENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismRoleContext.VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismScopeRelationContext.REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_JOIN_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeSectionDefinition.getRequiredSections;
import static com.zuehlke.pgadmissions.services.NotificationService.requestLimit;
import static com.zuehlke.pgadmissions.utils.PrismListUtils.processRowDescriptors;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.joda.time.DateTime.now;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
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
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismRoleContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismScopeRelationContext.PrismScopeRelation;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeSectionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.resource.ResourceStateDefinition;
import com.zuehlke.pgadmissions.domain.resource.ResourceStateTransitionSummary;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.OpportunityType;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceActivityDTO;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.dto.ResourceConnectionDTO;
import com.zuehlke.pgadmissions.dto.ResourceIdentityDTO;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.dto.ResourceOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.dto.ResourceSimpleDTO;
import com.zuehlke.pgadmissions.dto.UserNotificationDTO;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceConditionDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceRelationInvitationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceTargetDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
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
    private EntityService entityService;

    @Inject
    private PrismService prismService;

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
    public <T extends ResourceCreationDTO> ActionOutcomeDTO createResource(User user, Action action, T resourceDTO, boolean systemInvocation) {
        Scope scope = action.getCreationScope();

        ResourceCreator<T> resourceCreator = (ResourceCreator<T>) applicationContext.getBean(scope.getId().getResourceCreator());
        Resource resource = resourceCreator.create(user, resourceDTO);
        resource.setShared(scope.getDefaultShared());

        PrismState initialState = resourceDTO.getInitialState();
        Comment comment = new Comment().withResource(resource).withUser(user).withAction(action).withDeclinedResponse(false)
                .withTransitionState(initialState == null ? null : stateService.getById(initialState)).withCreatedTimestamp(new DateTime())
                .addAssignedUser(user, roleService.getCreatorRole(resource), CREATE);

        ActionOutcomeDTO outcome = null;
        if (systemInvocation) {
            outcome = actionService.executeAction(resource, action, comment);
        } else {
            outcome = actionService.executeUserAction(resource, action, comment);
        }

        createResourceTarget(resource, resourceDTO);
        return outcome;
    }

    public ResourceParent inviteResourceRelation(ResourceRelationInvitationDTO resourceRelationDTO) {
        ResourceParent resource = createResourceRelation(resourceRelationDTO, true);
        notificationService.sendOrganizationInvitationNotification(userService.getCurrentUser(), resource.getUser(), resource);
        return resource;
    }

    public ResourceParent createResourceRelation(ResourceRelationInvitationDTO resourceRelationDTO) {
        return createResourceRelation(resourceRelationDTO, false);
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

    @SuppressWarnings("unchecked")
    public <T extends ResourceCreationDTO> ActionOutcomeDTO executeAction(User user, CommentDTO commentDTO) {
        ActionOutcomeDTO actionOutcome = null;
        PrismRoleContext roleContext = commentDTO.getRoleContext();
        if (roleContext != null) {
            joinResource(commentDTO.getResource(), user, roleContext);
        } else if (commentDTO.getAction().getActionCategory().equals(CREATE_RESOURCE)) {
            T resourceDTO = (T) commentDTO.getResource();
            Action action = actionService.getById(commentDTO.getAction());
            resourceDTO.setParentResource(commentDTO.getResource().getParentResource());
            actionOutcome = createResource(user, action, resourceDTO, false);
        } else {
            Class<? extends ActionExecutor> actionExecutor = commentDTO.getAction().getScope().getActionExecutor();
            if (actionExecutor != null) {
                actionOutcome = applicationContext.getBean(actionExecutor).execute(commentDTO);
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
                    : stateService.getStateDurationConfiguration(resource, stateDurationDefinition);
            resource.setDueDate(baseline
                    .plusDays(stateDurationConfiguration == null ? 0 : stateDurationConfiguration.getDuration()));
        }
        entityService.flush();
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> void postProcessResource(T resource, Comment comment) {
        Class<? extends ResourceProcessor<T>> processor = (Class<? extends ResourceProcessor<T>>) resource.getResourceScope().getResourcePostprocessor();
        if (processor != null) {
            applicationContext.getBean(processor).process(resource, comment);
        }

        if (comment.isStateGroupTransitionComment() && comment.getAction().getCreationScope() == null) {
            createOrUpdateStateTransitionSummary(resource, new DateTime());
        }

        if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
            setResourceAdvertIncompleteSection((ResourceParent) resource);
        }

        entityService.flush();
    }

    public void executeUpdate(Resource resource, User user, PrismDisplayPropertyDefinition messageIndex, CommentAssignedUser... assignees) {
        Action action = actionService.getViewEditAction(resource);
        if (action != null) {
            Comment comment = new Comment().withUser(user).withAction(action)
                    .withContent(applicationContext.getBean(PropertyLoader.class).localizeLazy(resource).loadLazy(messageIndex))
                    .withDeclinedResponse(false).withCreatedTimestamp(new DateTime());

            for (CommentAssignedUser assignee : assignees) {
                comment.addAssignedUser(assignee.getUser(), assignee.getRole(), assignee.getRoleTransitionType());
                entityService.evict(assignee);
            }
            actionService.executeUserAction(resource, action, comment);
        }
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
    public List<ResourceListRowDTO> getResourceList(User user, PrismScope scope, List<PrismScope> parentScopes, ResourceListFilterDTO filter, Integer recordsToRetrieve,
            String sequenceId, Collection<Integer> resourceIds, Collection<Integer> onlyAsPartnerResourceIds, boolean extended) {
        if (!resourceIds.isEmpty()) {
            boolean hasRedactions = actionService.hasRedactions(user, scope);
            List<ResourceListRowDTO> rows = resourceDAO.getResourceList(user, scope, parentScopes, resourceIds, filter, sequenceId, recordsToRetrieve, hasRedactions);

            if (!rows.isEmpty()) {
                Map<Integer, ResourceListRowDTO> rowIndex = rows.stream().collect(Collectors.toMap(row -> (row.getResourceId()), row -> (row)));
                Set<Integer> filteredResourceIds = rowIndex.keySet();

                LinkedHashMultimap<Integer, PrismState> secondaryStates = extended ? stateService.getSecondaryResourceStates(scope, filteredResourceIds)
                        : LinkedHashMultimap.create();
                LinkedHashMultimap<Integer, ActionDTO> permittedActions = extended ? actionService.getPermittedActions(scope, filteredResourceIds, user)
                        : LinkedHashMultimap.create();

                Collection<Integer> filteredNativeOwnerResourceIds = ListUtils.removeAll(filteredResourceIds, onlyAsPartnerResourceIds);
                LinkedHashMultimap<Integer, ActionDTO> creationActions = actionService.getCreateResourceActions(scope, filteredNativeOwnerResourceIds);

                rowIndex.keySet().forEach(resourceId -> {
                    ResourceListRowDTO row = rowIndex.get(resourceId);
                    row.setSecondaryStateIds(Lists.newLinkedList(secondaryStates.get(resourceId)));

                    List<ActionDTO> actions = Lists.newLinkedList(permittedActions.get(resourceId));
                    actions.addAll(creationActions.get(resourceId));
                    row.setActions(actions);
                });
            }

            return rows;
        }

        return Lists.newArrayList();
    }

    public Map<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource resource, PrismScope propertiesScope) {
        Map<PrismDisplayPropertyDefinition, String> properties = Maps.newLinkedHashMap();
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localizeLazy(resource);
        for (PrismDisplayPropertyDefinition prismDisplayPropertyDefinition : PrismDisplayPropertyDefinition.getProperties(propertiesScope)) {
            properties.put(prismDisplayPropertyDefinition, loader.loadEager(prismDisplayPropertyDefinition));
        }
        return properties;
    }

    public List<Integer> getResourcesForWhichUserHasRoles(User user, PrismRole... roles) {
        return resourceDAO.getResourceForWhichUserHasRoles(user, roles);
    }

    public List<ResourceConnectionDTO> getResourcesForWhichUserCanMakeConnections(User user, String searchTerm) {
        Set<ResourceConnectionDTO> resources = Sets.newTreeSet();
        for (PrismScope resourceScope : new PrismScope[] { INSTITUTION, DEPARTMENT }) {
            resourceDAO.getResourcesForWhichUserCanConnect(user, resourceScope, searchTerm)
                    .forEach(resource -> resources.add(resource));
        }
        return new ArrayList<>(resources);
    }

    public List<Resource> getResourcesByUser(PrismScope prismScope, User user) {
        return resourceDAO.getResourcesByUser(prismScope, user);
    }

    public List<Integer> getResourcesByMatchingUserAndRole(PrismScope prismScope, String searchTerm, List<PrismRole> prismRoles) {
        return resourceDAO.getResourcesByMatchingUsersAndRole(prismScope, searchTerm, prismRoles);
    }

    public List<Integer> getSimilarResources(PrismScope enclosingResourceScope, String searchTerm) {
        return resourceDAO.getSimilarResources(enclosingResourceScope, searchTerm);
    }

    public List<ResourceChildCreationDTO> getResourcesForWhichUserCanCreateResource(Resource enclosingResource, PrismScope scope, PrismScope creationScope, String searchTerm) {
        User user = userService.getCurrentUser();

        String scopeReference = scope.getLowerCamelName();
        Set<ResourceChildCreationDTO> resources = Sets.newTreeSet();
        ResourceListFilterDTO filter = new ResourceListFilterDTO().withResourceIds(resourceDAO.getResourceIds(enclosingResource, scope, searchTerm));

        for (PrismScope actionScope : scopeService.getEnclosingScopesDescending(creationScope, scope)) {
            if (!actionScope.equals(creationScope)) {
                List<PrismScope> parentScopes = scopeService.getParentScopesDescending(actionScope, SYSTEM);

                Set<Integer> resourceIds = Sets.newHashSet();
                Map<String, Integer> summaries = Maps.newHashMap();
                Set<Integer> onlyAsPartnerResourceIds = Sets.newHashSet();
                Set<ResourceOpportunityCategoryDTO> scopedResources = getResources(user, actionScope, parentScopes, filter);
                processRowDescriptors(scopedResources, resourceIds, onlyAsPartnerResourceIds, summaries);

                for (ResourceListRowDTO row : getResourceList(user, actionScope, parentScopes, filter, null, null, resourceIds, onlyAsPartnerResourceIds, false)) {
                    ResourceChildCreationDTO resource = new ResourceChildCreationDTO();
                    resource.setScope(scope);

                    resource.setId((Integer) getProperty(row, scopeReference + "Id"));
                    resource.setName((String) getProperty(row, scopeReference + "Name"));

                    if (actionScope.equals(INSTITUTION)) {
                        resource.setLogoImageId(row.getLogoImageId());
                    }

                    row.getActions().forEach(action -> {
                        PrismAction prismAction = action.getActionId();
                        if (prismAction.getActionCategory().equals(CREATE_RESOURCE) && prismAction.name().endsWith(creationScope.name())) {
                            if (prismAction.getScope().equals(scope)) {
                                resource.setCreateDirectly(true);
                            }
                            resources.add(resource);
                        }
                    });
                }
            }
        }

        return newLinkedList(resources);
    }

    public ResourceStudyOption getResourceStudyOption(ResourceOpportunity resource, PrismStudyOption studyOption) {
        return resourceDAO.getResourceStudyOption(resource, studyOption);
    }

    public List<PrismStudyOption> getStudyOptions(ResourceOpportunity resource) {
        List<PrismStudyOption> filteredStudyOptions = Lists.newLinkedList();
        List<ResourceStudyOption> studyOptions = resourceDAO.getResourceStudyOptions(resource);

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

    public <T extends ResourceParent, U extends ResourceParentDTO> void setResourceAttributes(T resource, U resourceDTO) {
        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunity resourceOpportunity = (ResourceOpportunity) resource;
            ResourceOpportunityDTO resourceOpportunityDTO = (ResourceOpportunityDTO) resourceDTO;
            setResourceOpportunityType(resourceOpportunity, resourceOpportunityDTO.getOpportunityType());
            setStudyOptions(resourceOpportunity, resourceOpportunityDTO.getStudyOptions());
        } else {
            setResourceOpportunityCategories(resource, resourceDTO.getOpportunityCategories().stream()
                    .map(Enum::name).collect(joining("|")));
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
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_DEPARTMENT).withInternalMode(true).withExternalMode(true));
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_PROGRAM).withInternalMode(true).withExternalMode(true));
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_PROJECT).withInternalMode(true).withExternalMode(true));
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_APPLICATION).withInternalMode(false).withExternalMode(true));
                break;
            case DEPARTMENT:
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_PROGRAM).withInternalMode(true).withExternalMode(true));
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_PROJECT).withInternalMode(true).withExternalMode(true));
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_APPLICATION).withInternalMode(false).withExternalMode(true));
                break;
            case PROGRAM:
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_PROJECT).withInternalMode(true).withExternalMode(true));
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_APPLICATION).withInternalMode(false).withExternalMode(true));
                break;
            case PROJECT:
                resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_APPLICATION).withInternalMode(false).withExternalMode(true));
                break;
            default:
                throw new UnsupportedOperationException("Resource type " + resource.getResourceScope().name() + " does not have action conditions");
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

    public void setStudyOptions(ResourceOpportunity resource, List<PrismStudyOption> studyOptions) {
        if (resource.getId() != null) {
            resourceDAO.deleteResourceStudyOptions(resource);
            resource.getResourceStudyOptions().clear();
        }

        if (studyOptions == null) {
            studyOptions = studyOptions == null ? asList(PrismStudyOption.values()) : studyOptions;
        }

        resource.getResourceStudyOptions()
                .addAll(studyOptions.stream().map(studyOption -> new ResourceStudyOption().withResource(resource).withStudyOption(studyOption)).collect(Collectors.toList()));
    }

    public <T extends ResourceParentDTO> void updateResource(PrismScope resourceScope, Integer resourceId, ResourceOpportunityDTO resourceDTO) {
        ResourceOpportunity resource = (ResourceOpportunity) getById(resourceScope, resourceId);
        updateResource(resource, resourceDTO);

        resource.setAvailableDate(resourceDTO.getAvailableDate());
        resource.setDurationMinimum(resourceDTO.getDurationMinimum());
        resource.setDurationMaximum(resourceDTO.getDurationMaximum());
        setResourceOpportunityType(resource, resourceDTO.getOpportunityType());

        List<PrismStudyOption> studyOptions = resourceDTO.getStudyOptions();
        setStudyOptions(resource, studyOptions == null ? newArrayList() : studyOptions);
    }

    public <T extends ResourceParent, U extends ResourceParentDTO> void updateResource(T resource, U resourceDTO) {
        AdvertDTO advertDTO = resourceDTO.getAdvert();
        resource.setImportedCode(resourceDTO.getImportedCode());

        String name = resourceDTO.getName();
        Advert advert = resource.getAdvert();
        resource.setName(name);
        advert.setName(name);

        advert.setGloballyVisible(advertDTO.getGloballyVisible());
        advertService.updateAdvert(resource.getParentResource(), advert, advertDTO, resourceDTO.getName());

        List<ResourceConditionDTO> resourceConditions = resourceDTO.getConditions();
        setResourceConditions(resource, resourceConditions == null ? Lists.newArrayList() : resourceConditions);
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

    public ResourceRepresentationRobotMetadata getResourceRobotMetadataRepresentation(Resource resource, List<PrismState> scopeStates,
            HashMultimap<PrismScope, PrismState> enclosedScopes) {
        return resourceDAO.getResourceRobotMetadataRepresentation(resource, scopeStates, enclosedScopes);
    }

    public ResourceRepresentationRobotMetadataRelated getResourceRobotRelatedRepresentations(Resource resource, PrismScope relatedScope, String label) {
        HashMultimap<PrismScope, PrismState> childScopes = scopeService.getChildScopesWithActiveStates(relatedScope, PROJECT);
        List<ResourceRepresentationIdentity> childResources = resourceDAO.getResourceRobotRelatedRepresentations(
                resource, relatedScope, stateService.getActiveResourceStates(relatedScope), childScopes);
        return childResources.isEmpty() ? null
                : new ResourceRepresentationRobotMetadataRelated().withLabel(label).withResources(childResources);
    }

    public ResourceActivityDTO getResourceWithParentResources(Resource resource, List<PrismScope> parentScopes) {
        return resourceDAO.getResourceWithParentResources(resource, parentScopes);
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

            commentService.getResourceOwnerComments(resource).forEach(oldComment -> {
                commentUserProperties.forEach(commentUserProperty -> {
                    userService.mergeUserAssignment(oldComment, newUser, commentUserProperty);
                });
            });

            commentService.getResourceOwnerCommentAssignedUsers(resource).forEach(oldCommentAssignedUser -> {
                commentAssignedUserUserProperties.forEach(commentAssignedUserProperty -> {
                    userService.mergeUserAssignment(oldCommentAssignedUser, newUser, commentAssignedUserProperty);
                });
            });

            documentService.getResourceOwnerDocuments(resource).forEach(oldDocument -> {
                documentUserProperties.forEach(documentUserProperty -> {
                    userService.mergeUserAssignment(oldDocument, newUser, documentUserProperty);
                });
            });
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

    public Set<ResourceOpportunityCategoryDTO> getResources(User user, PrismScope scope, List<PrismScope> parentScopes) {
        return getResources(user, scope, parentScopes, null, null);
    }

    public Set<ResourceOpportunityCategoryDTO> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, ResourceListFilterDTO filter) {
        filter = resourceListFilterService.saveOrGetByUserAndScope(user, scope, filter);
        return getResources(user, scope, parentScopes, filter, getFilterConditions(scope, filter));
    }

    public <T> Set<T> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, ResourceListFilterDTO filter, ProjectionList columns, Class<T> responseClass) {
        return getResources(user, scope, parentScopes, filter, columns, getFilterConditions(scope, filter), responseClass);
    }

    public List<ResourceSimpleDTO> getResources(Resource enclosingResource, PrismScope resourceScope, Optional<String> query) {
        return resourceDAO.getResources(enclosingResource, resourceScope, query);
    }

    public User joinResource(ResourceParent resource, UserDTO userDTO, PrismRoleContext roleContext) {
        User user = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        joinResource(resource, user, roleContext);
        return user;
    }

    public void joinResource(ResourceCreationDTO resource, User user, PrismRoleContext roleContext) {
        joinResource((ResourceParent) getById(resource.getScope(), resource.getId()), user, roleContext);
    }

    public void joinResource(ResourceParent resource, User user, PrismRoleContext roleContext) {
        User currentUser = userService.getCurrentUser();
        Action viewEditAction = actionService.getViewEditAction(resource);
        boolean canViewEdit = viewEditAction == null ? false : actionService.checkActionExecutable(resource, viewEditAction, currentUser, false);

        Role role = null;
        String resourceName = resource.getResourceScope().name();
        if (roleContext.equals(STUDENT)) {
            role = roleService.getById(PrismRole.valueOf(resourceName + "_STUDENT" + (canViewEdit ? "" : "_UNVERIFIED")));
            roleService.getOrCreateUserRole(new UserRole().withResource(resource).withUser(user).withRole(role).withAssignedTimestamp(now()));
        } else {
            role = roleService.getById(PrismRole.valueOf(resourceName + "_VIEWER" + (canViewEdit ? "" : "_UNVERIFIED")));
            roleService.getOrCreateUserRole(new UserRole().withResource(resource).withUser(user).withRole(role).withAssignedTimestamp(now()));
        }

        if (canViewEdit && role != null) {
            executeUpdate(resource, currentUser, PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_USER_ROLE"),
                    new CommentAssignedUser().withUser(user).withRole(role).withRoleTransitionType(CREATE));
        } else if (!canViewEdit) {
            List<User> admins = userService.getResourceUsers(resource, PrismRole.valueOf(resourceName + "_ADMINISTRATOR"));
            Map<UserNotificationDTO, Integer> recentRequests = notificationService.getRecentRequests(admins.stream().map(a -> a.getId()).collect(toList()), LocalDate.now());

            admins.forEach(admin -> {
                Integer recentRequestCount = recentRequests.get(new UserNotificationDTO().withUserId(admin.getId()).withNotificationDefinitionId(SYSTEM_JOIN_REQUEST));
                if (recentRequestCount == null || recentRequestCount <= requestLimit) {
                    notificationService.sendJoinRequest(user, admin, resource);
                }
            });
        }
    }

    public void activateResource(User user, ResourceParent resource) {
        String scopePrefix = resource.getResourceScope().name();
        Action action = actionService.getById(PrismAction.valueOf(scopePrefix + "_COMPLETE_APPROVAL_STAGE"));

        if (actionService.getActions(resource).contains(action)) {
            String approvedMessage = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem())
                    .loadLazy(PrismDisplayPropertyDefinition.valueOf(scopePrefix + "_COMMENT_APPROVED"));
            actionService.executeAction(resource, action, new Comment().withUser(user).withAction(action).withContent(approvedMessage).withDeclinedResponse(false)
                    .withTransitionState(stateService.getById(PrismState.valueOf(scopePrefix + "_APPROVED"))).withCreatedTimestamp(new DateTime()));
        }
    }

    public List<Integer> getResourcesWithUsersToVerify(PrismScope resourceScope) {
        return resourceDAO.getResourcesWithUsersToVerify(resourceScope);
    }

    public List<Integer> getResourcesWithNewOpportunities(PrismScope resourceScope, PrismScope targeterScope, PrismScope targetScope, DateTime createdBaseline) {
        return resourceDAO.getResourcesWithNewOpportunities(resourceScope, targeterScope, targetScope, createdBaseline);
    }

    public ResourceParent getResourceParent(ResourceParent resource) {
        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            return getResourceParent(resource.getParentResource());
        }
        return resource;
    }

    public boolean isUnderApproval(ResourceParent resource) {
        List<PrismState> states = stateService.getResourceStates(resource);
        return states.stream().filter(s -> s.name().contains("APPROVAL")).count() > 0;
    }

    public HashMultimap<PrismScope, Integer> getResourcesUserCanAdminister(User user) {
        HashMultimap<PrismScope, Integer> resources = HashMultimap.create();

        List<PrismScope> visibleScopes = roleService.getVisibleScopes(user);
        for (PrismScope scope : visibleScopes) {
            String scopeReference = scope.name();

            getResources(user, scope, visibleScopes.stream()
                    .filter(as -> as.ordinal() < scope.ordinal())
                    .collect(Collectors.toList()), //
                    new ResourceListFilterDTO().withRoleCategory(ADMINISTRATOR).withActionId(PrismAction.valueOf(scopeReference + "_VIEW_EDIT"))
                            .withActionEnhancements(actionService.getAdministratorActionEnhancements(scope)), //
                    Projections.projectionList() //
                            .add(Projections.groupProperty("action.scope.id").as("scope")) //
                            .add(Projections.groupProperty("resource.id").as("id")),
                    ResourceIdentityDTO.class).forEach(resource -> {
                        resources.put(resource.getScope(), resource.getId());
                    });
        }

        return resources;
    }

    private Set<ResourceOpportunityCategoryDTO> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, ResourceListFilterDTO filter, Junction conditions) {
        return getResources(user, scope, parentScopes, filter, //
                Projections.projectionList() //
                        .add(Projections.groupProperty("resource.id").as("id")) //
                        .add(Projections.max("stateAction.raisesUrgentFlag").as("raisesUrgentFlag")) //
                        .add(Projections.property("resource.opportunityCategories").as("opportunityCategories")), //
                conditions, ResourceOpportunityCategoryDTO.class);
    }

    private <T> Set<T> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, ResourceListFilterDTO filter, ProjectionList columns, Junction conditions,
            Class<T> responseClass) {
        Set<T> resources = Sets.newHashSet();
        DateTime baseline = DateTime.now().minusDays(1);

        Boolean asPartner = responseClass.equals(ResourceOpportunityCategoryDTO.class) ? false : null;
        addResources(resourceDAO.getResources(user, scope, filter, columns, conditions, responseClass, baseline), resources, asPartner);

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScope : parentScopes) {
                addResources(resourceDAO.getResources(user, scope, parentScope, filter, columns, conditions, responseClass, baseline), resources, asPartner);
            }

            asPartner = asPartner == null ? null : true;
            for (PrismScope targeterScope : targetScopes) {
                if (scope.ordinal() > targeterScope.ordinal()) {
                    for (PrismScope targetScope : targetScopes) {
                        addResources(resourceDAO.getResources(user, scope, targeterScope, targetScope, filter, columns, conditions, responseClass, baseline), resources, asPartner);
                    }
                }
            }
        }

        return resources;
    }

    private <T> void addResources(List<T> resources, Set<T> resourcesFiltered, Boolean asPartner) {
        boolean processOnlyAsPartner = asPartner != null;
        resources.forEach(resource -> {
            resourcesFiltered.add(resource);
            if (processOnlyAsPartner) {
                ((ResourceOpportunityCategoryDTO) resource).setOnlyAsPartner(asPartner);
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

    private <T extends ResourceStateDefinition, U extends CommentStateDefinition> void deleteResourceStates(Set<T> resourceStateDefinitions, Set<U> commentStateDefinitions) {
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

    private void setResourceOpportunityType(ResourceOpportunity resourceOpportunity, PrismOpportunityType prismOpportunityType) {
        OpportunityType opportunityType = prismService.getOpportunityTypeById(prismOpportunityType);
        resourceOpportunity.setOpportunityType(opportunityType);

        Advert advert = resourceOpportunity.getAdvert();
        advert.setOpportunityType(opportunityType);

        String opportunityCategory = opportunityType.getOpportunityCategory().name();
        resourceOpportunity.setOpportunityCategories(opportunityCategory);
        advert.setOpportunityCategories(opportunityCategory);

        for (PrismScope scope : new PrismScope[] { DEPARTMENT, INSTITUTION }) {
            ResourceParent enclosing = (ResourceParent) resourceOpportunity.getEnclosingResource(scope);
            if (enclosing != null) {
                String opportunityCategories = enclosing.getOpportunityCategories();
                if (opportunityCategories == null) {
                    setResourceOpportunityCategories(enclosing, opportunityCategory);
                } else {
                    Set<String> opportunityCategoriesSplit = Sets.newHashSet(opportunityCategories.split("\\|"));
                    opportunityCategoriesSplit.add(opportunityCategory);
                    setResourceOpportunityCategories(enclosing, Joiner.on("|").join(opportunityCategoriesSplit));
                }
            }
        }
    }

    private void setResourceOpportunityCategories(ResourceParent resource, String opportunityCategories) {
        resource.setOpportunityCategories(opportunityCategories);
        resource.getAdvert().setOpportunityCategories(opportunityCategories);
    }

    private <T extends ResourceCreationDTO> void createResourceTarget(Resource resource, T resourceDTO) {
        if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
            ResourceTargetDTO target = ((ResourceParentDTO) resourceDTO).getAdvert().getTarget();
            if (target != null) {
                advertService.createAdvertTarget(resource.getResourceScope(), resource.getId(), target);
            }
        }
    }

    private ResourceParent createResourceRelation(ResourceRelationInvitationDTO resourceRelationDTO, boolean invitation) {
        if (validateResourceFamilyCreation(resourceRelationDTO)) {
            ResourceParent resource = null;

            User assignUser = null;
            User currentUser = userService.getCurrentUser();
            User resourceUser = systemService.getSystem().getUser();

            boolean assignedUsers = false;
            for (ResourceCreationDTO resourceDTO : resourceRelationDTO.getResources()) {
                Integer thisId = resourceDTO.getId();

                PrismScope thisScope = resourceDTO.getScope();
                PrismScope lastScope = resource == null ? SYSTEM : resource.getResourceScope();

                resourceDTO.setContext(resourceRelationDTO.getContext().getContext());
                if (thisId == null) {
                    resourceDTO.setInitialState(PrismState.valueOf(thisScope.name() + "_UNSUBMITTED"));
                    if (resource != null) {
                        resourceDTO.setParentResource(new ResourceDTO().withScope(lastScope).withId(resource.getId()));
                    }

                    if (!assignedUsers && thisScope.getScopeCategory().equals(OPPORTUNITY)) {
                        UserDTO userDTO = resourceRelationDTO.getUser();
                        if (userDTO != null) {
                            assignUser = joinResource(resource, userDTO, VIEWER);
                        }

                        if (resourceRelationDTO.getContext().equals(APPLICANT)) {
                            joinResource(resource, currentUser, STUDENT);
                        }

                        assignedUsers = true;
                    }

                    User owner;
                    if (invitation) {
                        UserDTO userDTO = resourceRelationDTO.getUser();
                        owner = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
                    } else if (thisScope.equals(PROJECT)) {
                        owner = resourceRelationDTO.getContext().equals(REFEREE) ? assignUser : currentUser;
                    } else {
                        owner = resourceUser;
                    }

                    Action action = actionService.getById(PrismAction.valueOf(lastScope.name() + "_CREATE_" + thisScope.name()));
                    resource = (ResourceParent) createResource(owner, action, resourceDTO, true).getResource();
                } else {
                    resource = (ResourceParent) getById(thisScope, thisId);
                    createResourceTarget(resource, resourceDTO);
                    resourceUser = resource.getUser();
                }
            }

            return resource;
        }

        throw new UnsupportedOperationException("Invalid resource relation creation attempt");
    }

    private boolean validateResourceFamilyCreation(ResourceRelationInvitationDTO resourceRelationDTO) {
        List<PrismScope> scopes = resourceRelationDTO.getResources().stream().map(r -> r.getScope()).collect(toList());
        for (PrismScopeRelation relation : resourceRelationDTO.getContext().getRelations()) {
            if (relation.stream().map(creation -> creation.getScope()).collect(Collectors.toList()).containsAll(scopes)) {
                return true;
            }
        }
        return false;
    }

}
