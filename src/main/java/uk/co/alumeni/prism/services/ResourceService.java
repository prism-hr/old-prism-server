package uk.co.alumeni.prism.services;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import jersey.repackaged.com.google.common.base.Objects;
import jersey.repackaged.com.google.common.collect.Iterables;
import jersey.repackaged.com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.util.Assert;
import uk.co.alumeni.prism.dao.ResourceDAO;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.comment.*;
import uk.co.alumeni.prism.domain.definitions.*;
import uk.co.alumeni.prism.domain.definitions.PrismResourceRelationContext.PrismResourceRelation;
import uk.co.alumeni.prism.domain.definitions.workflow.*;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.*;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.*;
import uk.co.alumeni.prism.dto.*;
import uk.co.alumeni.prism.dto.ResourceConnectionDTO;
import uk.co.alumeni.prism.exceptions.PrismForbiddenException;
import uk.co.alumeni.prism.exceptions.WorkflowEngineException;
import uk.co.alumeni.prism.rest.dto.ReplicableActionSequenceDTO;
import uk.co.alumeni.prism.rest.dto.StateActionPendingDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.resource.*;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationIdentity;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRobotMetadata;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRobotMetadataRelated;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSitemap;
import uk.co.alumeni.prism.services.builders.PrismResourceListConstraintBuilder;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;
import uk.co.alumeni.prism.workflow.evaluators.ResourceCompletenessEvaluator;
import uk.co.alumeni.prism.workflow.executors.action.ActionExecutor;
import uk.co.alumeni.prism.workflow.resolvers.state.duration.StateDurationResolver;
import uk.co.alumeni.prism.workflow.transition.creators.ResourceCreator;
import uk.co.alumeni.prism.workflow.transition.populators.ResourcePopulator;
import uk.co.alumeni.prism.workflow.transition.processors.ResourceProcessor;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.joda.time.DateTime.now;
import static uk.co.alumeni.prism.dao.WorkflowDAO.organizationScopes;
import static uk.co.alumeni.prism.domain.definitions.PrismFilterMatchMode.ANY;
import static uk.co.alumeni.prism.domain.definitions.PrismRoleContext.STUDENT;
import static uk.co.alumeni.prism.domain.definitions.PrismRoleContext.VIEWER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeSectionDefinition.getRequiredSections;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateActionPendingType.ACTION;
import static uk.co.alumeni.prism.utils.PrismListUtils.getRowsToReturn;
import static uk.co.alumeni.prism.utils.PrismListUtils.processRowDescriptors;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;

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
    private ActivityService activityService;

    @Inject
    private AdvertService advertService;

    @Inject
    private ApplicationService applicationService;

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

        ActionOutcomeDTO outcome;
        if (systemInvocation) {
            outcome = actionService.executeAction(resource, action, comment);
        } else {
            outcome = actionService.executeUserAction(resource, action, comment);
        }

        if (ResourceParentDTO.class.isAssignableFrom(resourceDTO.getClass())) {
            ResourceParentDTO resourceParentDTO = (ResourceParentDTO) resourceDTO;
            ResourceRelationCreationDTO target = resourceParentDTO.getTarget();
            if (target != null) {
                advertService.createAdvertTarget((ResourceParent) resource, target);
            }
            advertService.updateAdvertVisibility(resource.getAdvert(), resourceParentDTO);
            Assert.notNull(resource.getAdvert().getGloballyVisible());
        }


        return outcome;
    }

    public ResourceParent inviteResourceRelation(Resource resource, User user, ResourceRelationCreationDTO resourceInvitationDTO) {
        return inviteResourceRelation(resource, user, resourceInvitationDTO, resourceInvitationDTO.getMessage());
    }

    public ResourceParent inviteResourceRelation(Resource resource, User user, ResourceRelationCreationDTO resourceInvitationDTO, String message) {
        if (validateResourceRelationCreation(resourceInvitationDTO)) {
            User childOwner = userService.getOrCreateUser(resourceInvitationDTO.getUser());

            AdvertTarget target = null;
            PrismResourceContext context = resourceInvitationDTO.getContext().getContext();
            ResourceParent resourceTarget = createResourceRelation(resourceInvitationDTO.getResource(), context, childOwner);
            if (resource != null) {
                target = advertService.createAdvertTarget((ResourceParent) resource, user, resourceTarget, resourceTarget.getUser(), context, message);
            }

            notificationService.sendOrganizationInvitationNotification(user, resourceTarget.getUser(), resourceTarget, target, message);
            return resourceTarget;
        }

        throw new UnsupportedOperationException("Invalid resource relation invitation attempt");
    }

    public ResourceParent createResourceRelation(ResourceRelationCreationDTO resourceRelationCreationDTO) {
        if (validateResourceRelationCreation(resourceRelationCreationDTO)) {
            User viewer = null;
            User student = null;
            User childOwner = null;
            switch (resourceRelationCreationDTO.getContext()) {
                case QUALIFICATION:
                    viewer = userService.getOrCreateUser(resourceRelationCreationDTO.getUser());
                    student = userService.getCurrentUser();
                    childOwner = student;
                    break;
                case EMPLOYMENT_POSITION:
                    childOwner = userService.getCurrentUser();
                    break;
                case REFEREE:
                    viewer = userService.getOrCreateUser(resourceRelationCreationDTO.getUser());
                    childOwner = viewer;
                    break;
                default:
                    throw new UnsupportedOperationException("Invalid resource relation creation attempt");
            }

            ResourceParent resource = createResourceRelation(resourceRelationCreationDTO.getResource(), resourceRelationCreationDTO.getContext().getContext(), childOwner);
            ResourceParent parentResource = firstNonNull(resource.getDepartment(), resource.getInstitution());

            if (viewer != null) {
                joinResource(parentResource, viewer, VIEWER);
            }

            if (student != null) {
                joinResource(parentResource, student, STUDENT);
            }

            return resource;
        }

        throw new UnsupportedOperationException("Invalid resource relation creation attempt");
    }

    public ResourceParent createResourceRelation(ResourceRelationDTO resourceRelationDTO, PrismResourceContext context, User childOwner) {
        Resource resource = systemService.getSystem();
        User owner = resource.getUser();

        List<ResourceCreationDTO> resourceDTOs = resourceRelationDTO.getResources();
        PrismScope finalScope = Iterables.getLast(resourceDTOs).getScope();
        for (ResourceCreationDTO resourceDTO : resourceDTOs) {
            Integer thisId = resourceDTO.getId();
            PrismScope thisScope = resourceDTO.getScope();

            Integer lastId = resource.getId();
            PrismScope lastScope = resource.getResourceScope();

            ResourceParent duplicateResource = null;
            owner = finalScope.equals(thisScope) ? childOwner : owner;
            if (thisId == null && thisScope.equals(PROJECT)) {
                duplicateResource = getActiveResourceByName(resource, thisScope, ((ResourceParentDTO) resourceDTO).getName());
            }

            resourceDTO.setContext(context);
            if (thisId == null && duplicateResource == null) {
                resourceDTO.setInitialState(PrismState.valueOf(thisScope.name() + "_UNSUBMITTED"));
                if (resource != null) {
                    resourceDTO.setParentResource(new ResourceDTO().withScope(lastScope).withId(lastId));
                }

                Action action = actionService.getById(PrismAction.valueOf(lastScope.name() + "_CREATE_" + thisScope.name()));
                resource = createResource(owner, action, resourceDTO, true).getResource();
            } else {
                if (thisId != null) {
                    resource = getById(thisScope, thisId);
                } else if (duplicateResource != null) {
                    resource = duplicateResource;
                }

                if (resource.getResourceScope().equals(finalScope)) {
                    Role role = roleService.getById(PrismRole.valueOf(finalScope.name() + "_ADMINISTRATOR"));
                    roleService.getOrCreateUserRole(new UserRole().withResource(resource).withUser(childOwner).withRole(role).withAssignedTimestamp(now()));
                } else {
                    owner = resource.getUser();
                }
            }
        }

        return (ResourceParent) resource;
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

            activityService.setSequenceIdentifier(resource, baseline);
            Class<? extends ResourcePopulator<T>> populator = (Class<? extends ResourcePopulator<T>>) resource.getResourceScope().getResourcePopulator();
            if (populator != null) {
                applicationContext.getBean(populator).populate(resource);
            }

            resource.setCode(generateResourceCode(resource));
            entityService.flush();
        } else if (comment.isUserComment() || resource.getSequenceIdentifier() == null) {
            resource.setUpdatedTimestamp(baseline);
            activityService.setSequenceIdentifier(resource, baseline);
            entityService.flush();
        }
    }

    public ActionOutcomeDTO executeAction(User user, CommentDTO commentDTO) {
        ActionOutcomeDTO actionOutcome = null;
        if (commentDTO.isBypassComment()) {
            executeActionBypass(user, commentDTO);
        } else if (commentDTO.isCreateComment()) {
            ResourceCreationDTO resourceDTO = commentDTO.getResource();
            Action action = actionService.getById(commentDTO.getAction());
            resourceDTO.setParentResource(commentDTO.getResource().getParentResource());
            actionOutcome = createResource(user, action, resourceDTO, false);
        } else {
            if (commentDTO.isClaimComment()) {
                commentService.preprocessClaimComment(user, commentDTO);
            }

            Class<? extends ActionExecutor> actionExecutor = commentDTO.getAction().getScope().getActionExecutor();
            if (actionExecutor != null) {
                actionOutcome = applicationContext.getBean(actionExecutor).execute(commentDTO);

                // TODO remove it later (should be called from executor)
//                if (commentDTO.isViewEditComment()) {
//                    updateAdvertVisibility(actionOutcome.getResource(), commentDTO.getResource());
//                }
            }

        }
        return actionOutcome;
    }

    public void executeActionBypass(User user, CommentDTO commentDTO) {
        PrismRoleContext roleContext = commentDTO.getRoleContext();
        ResourceRelationCreationDTO resourceInvitation = commentDTO.getResourceInvitation();
        if (roleContext != null) {
            joinResource(commentDTO.getResource(), user, roleContext);
        } else if (resourceInvitation != null) {
            Resource resourceInviting = null;
            ResourceCreationDTO resourceInvitingDTO = commentDTO.getResourceInviting();
            if (resourceInvitingDTO != null) {
                resourceInviting = getById(resourceInvitingDTO.getScope(), resourceInvitingDTO.getId());
            }
            inviteResourceRelation(resourceInviting, user, resourceInvitation);
        }
    }

    public void prepareActions(ReplicableActionSequenceDTO sequenceDTO) {
        List<Integer> resourceIds = sequenceDTO.getResources();
        List<Integer> commentIds = sequenceDTO.getTemplateComments();
        if (CollectionUtils.isNotEmpty(resourceIds) && CollectionUtils.isNotEmpty(commentIds)) {
            List<Comment> comments = commentService.getComments(commentIds);

            boolean validTransition = false;
            boolean validStartTransition = false;
            boolean validCloseTransition = false;

            int commentsSize = comments.size();
            for (int i = 0; i < commentsSize; i++) {
                Comment comment = comments.get(i);
                if (!Objects.equal(comment.getState(), comment.getTransitionState())) {
                    validTransition = true;

                    if (i == 0) {
                        StateAction stateAction = stateService.getStateAction(comment.getState(), comment.getAction());
                        validStartTransition = BooleanUtils.toBoolean(stateAction.getReplicableSequenceStart());
                    } else if (i == (commentsSize - 1)) {
                        StateTransition stateTransition = stateService.getStateTransition(comment.getState(), comment.getAction(), comment.getTransitionState());
                        validCloseTransition = BooleanUtils.toBoolean(stateTransition.getReplicableSequenceClose());
                    }
                } else {
                    validTransition = false;
                }
            }

            if (validTransition && validStartTransition && validCloseTransition) {
                List<PrismAction> actions = comments.stream().map(comment -> comment.getAction().getId()).collect(Collectors.toList());
                resourceIds.removeAll(resourceDAO.getResourcesWithStateActionsPending(actions.get(0).getScope(), actions));

                boolean authentic = true;
                User user = userService.getCurrentUser();
                for (Comment comment : comments) {
                    if (!Objects.equal(user, comment.getUser())) {
                        authentic = false;
                    }
                }

                if (authentic) {
                    resourceIds.stream().forEach(resourceId -> {
                        comments.stream().forEach(comment -> {
                            Action action = comment.getAction();
                            Resource resource = getById(action.getScope().getId(), resourceId);
                            StateActionPendingDTO stateActionPendingDTO = new StateActionPendingDTO();

                            Set<CommentAssignedUser> commentAssignedUsers = comment.getAssignedUsers();
                            if (CollectionUtils.isNotEmpty(commentAssignedUsers)) {
                                PrismRole assignUserRole = null;
                                List<UserDTO> assignUserList = Lists.newLinkedList();
                                for (CommentAssignedUser commentAssignedUser : commentAssignedUsers) {
                                    if (commentAssignedUser.getRoleTransitionType().equals(CREATE)) {
                                        if (assignUserRole == null) {
                                            assignUserRole = commentAssignedUser.getRole().getId();
                                        }

                                        assignUserList.add(userService.getUserDTO(commentAssignedUser.getUser()));
                                    }
                                }

                                if (assignUserRole != null) {
                                    stateActionPendingDTO.setAssignUserRole(assignUserRole);
                                    stateActionPendingDTO.setAssignUserList(assignUserList);
                                }
                            }

                            stateService.createStateActionPending(resource, user, action, comment.getTransitionState(), ACTION, stateActionPendingDTO);
                        });
                    });
                }
            }
        }
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
        if (comment.isStateTransitionComment() || (stateDurationDefinition != null && BooleanUtils.isTrue(stateDurationDefinition.getEscalation()))) {
            PrismStateDurationEvaluation stateDurationEvaluation = resource.getState().getStateDurationEvaluation();
            if (stateDurationEvaluation != null) {
                StateDurationResolver<T> resolver = (StateDurationResolver<T>) applicationContext.getBean(stateDurationEvaluation.getResolver());
                resource.setDueDate(resolver.resolve(resource, comment));
            } else if (stateDurationDefinition != null) {
                StateDurationConfiguration stateDurationConfiguration = stateService.getStateDurationConfiguration(resource, stateDurationDefinition);
                if (stateDurationConfiguration != null) {
                    resource.setDueDate(LocalDate.now().plusDays(stateDurationConfiguration.getDuration()));
                }
            }
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
        executeUpdate(resource, user, messageIndex, null, assignees);
    }

    public void executeUpdate(Resource resource, User user, PrismDisplayPropertyDefinition messageIndex, PrismState transitionStateId, CommentAssignedUser... assignees) {
        Action action = actionService.getViewEditAction(resource);
        if (action != null) {
            State transitionState = transitionStateId == null ? null : stateService.getById(transitionStateId);
            Comment comment = new Comment().withUser(user).withAction(action)
                    .withContent(applicationContext.getBean(PropertyLoader.class).localizeLazy(resource).loadLazy(messageIndex))
                    .withDeclinedResponse(false).withTransitionState(transitionState).withCreatedTimestamp(new DateTime());

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

    public Collection<ResourceListRowDTO> getResourceList(User user, PrismScope scope, List<PrismScope> parentScopes, Collection<Integer> targeterEntities,
                                                          Collection<ResourceOpportunityCategoryDTO> resourceDTOs, ResourceListFilterDTO filter, String lastSequenceIdentifier, Integer maxRecords,
                                                          Collection<Integer> onlyAsPartnerResources, boolean extended) {
        if (!resourceDTOs.isEmpty()) {
            boolean hasRedactions = actionService.hasRedactions(user, scope);
            Set<Integer> resources = getRowsToReturn(resourceDTOs, filter.getOpportunityCategory(), filter.getOpportunityTypes(), lastSequenceIdentifier, maxRecords).keySet();
            List<ResourceListRowDTO> rowDTOs = resourceDAO.getResourceList(user, scope, parentScopes, resources, filter, hasRedactions);

            if (!rowDTOs.isEmpty()) {
                TreeMap<String, ResourceListRowDTO> rows = Maps.newTreeMap();

                Map<Integer, ResourceListRowDTO> rowIndex = rowDTOs.stream().collect(Collectors.toMap(row -> (row.getResourceId()), row -> (row)));
                Set<Integer> filteredResources = rowIndex.keySet();

                LinkedHashMultimap<Integer, PrismState> secondaryStates = extended ? stateService.getSecondaryResourceStates(scope, filteredResources)
                        : LinkedHashMultimap.create();

                TreeMultimap<Integer, ActionDTO> permittedActions = extended ? actionService.getPermittedActions(user, scope, targeterEntities, filteredResources)
                        : TreeMultimap.create();

                TreeMultimap<Integer, ActionDTO> creationActions = actionService.getCreateResourceActions(scope, filteredResources);

                rowIndex.keySet().forEach(resourceId -> {
                    ResourceListRowDTO rowDTO = rowIndex.get(resourceId);
                    rowDTO.setSecondaryStateIds(Lists.newLinkedList(secondaryStates.get(resourceId)));

                    Set<ActionDTO> actions = Sets.newTreeSet(permittedActions.get(resourceId));

                    boolean onlyAsPartner = onlyAsPartnerResources.contains(resourceId);
                    creationActions.get(resourceId).forEach(creationAction -> {
                        if (!onlyAsPartner || creationAction.getActionId().name().endsWith("_CREATE_APPLICATION")) {
                            actions.add(creationAction);
                        }
                    });

                    rowDTO.setActions(Lists.newLinkedList(actions));

                    Boolean urgent = BooleanUtils.toBoolean(actions.stream().anyMatch(action -> BooleanUtils.toBoolean(action.getRaisesUrgentFlag())));
                    String sequenceIdentifier = (urgent ? 1 : 0) + rowDTO.getSequenceIdentifier();
                    rowDTO.setSequenceIdentifier(sequenceIdentifier);
                    rows.put(sequenceIdentifier, rowDTO);
                });

                return rows.descendingMap().values();
            }

            return Lists.newArrayList();
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

    public List<Resource> getResourcesByUser(PrismScope prismScope, User user) {
        return resourceDAO.getResourcesByUser(prismScope, user);
    }

    public List<Integer> getResourcesByUserAndRole(PrismScope prismScope, String searchTerm, List<PrismRole> prismRoles) {
        return resourceDAO.getResourcesByUserAndRole(prismScope, searchTerm, prismRoles);
    }

    public List<Integer> getSimilarResources(PrismScope enclosingResourceScope, String searchTerm) {
        return resourceDAO.getSimilarResources(enclosingResourceScope, searchTerm);
    }

    public List<Integer> getResourcesForWhichUserHasRoles(User user, PrismRole... roles) {
        return resourceDAO.getResourceForWhichUserHasRoles(user, roles);
    }

    public List<Integer> getResourcesForWhichUserHasRoles(User user, Collection<PrismRole> roles) {
        return resourceDAO.getResourceForWhichUserHasRoles(user, roles.toArray(new PrismRole[roles.size()]));
    }

    public Integer getResourceForWhichUserCanConnect(User user, ResourceParent resource) {
        return resourceDAO.getResourceForWhichUserCanConnect(user, resource);
    }

    public List<ResourceConnectionDTO> getResourcesForWhichUserCanConnect(User user, String searchTerm) {
        Set<ResourceConnectionDTO> resources = Sets.newTreeSet();
        for (PrismScope resourceScope : new PrismScope[]{INSTITUTION, DEPARTMENT}) {
            resourceDAO.getResourcesForWhichUserCanConnect(user, resourceScope, searchTerm).forEach(resource -> resources.add(resource));
        }
        return new ArrayList<>(resources);
    }

    public List<ResourceChildCreationDTO> getResourcesForWhichUserCanCreateResource(Resource enclosingResource, PrismScope responseScope, PrismScope creationScope) {
        return getResourcesForWhichUserCanCreateResource(enclosingResource, responseScope, creationScope, null);
    }

    public List<ResourceChildCreationDTO> getResourcesForWhichUserCanCreateResource(Resource enclosingResource, PrismScope responseScope, PrismScope creationScope,
                                                                                    String searchTerm) {
        User user = userService.getCurrentUser();

        String scopeReference = responseScope.getLowerCamelName();
        Set<ResourceChildCreationDTO> resources = Sets.newTreeSet();
        ResourceListFilterDTO filter = new ResourceListFilterDTO().withResourceIds(resourceDAO.getResourceIds(enclosingResource, responseScope, searchTerm));

        for (PrismScope scope : scopeService.getEnclosingScopesDescending(creationScope, responseScope)) {
            if (!scope.equals(creationScope)) {
                List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);

                Map<String, Integer> summaries = Maps.newHashMap();
                Set<Integer> onlyAsPartnerResources = Sets.newHashSet();
                List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(user, scope);
                Set<ResourceOpportunityCategoryDTO> scopedResources = getResources(user, scope, parentScopes, targeterEntities, filter);
                processRowDescriptors(scopedResources, onlyAsPartnerResources, summaries);

                for (ResourceListRowDTO row : getResourceList(user, scope, parentScopes, targeterEntities, scopedResources, filter, null, null, onlyAsPartnerResources, false)) {
                    ResourceChildCreationDTO resource = new ResourceChildCreationDTO();
                    resource.setScope(responseScope);

                    resource.setId((Integer) getProperty(row, scopeReference + "Id"));
                    resource.setName((String) getProperty(row, scopeReference + "Name"));

                    if (scope.equals(INSTITUTION)) {
                        resource.setLogoImageId(row.getLogoImageId());
                    }

                    row.getActions().forEach(action -> {
                        PrismAction prismAction = action.getActionId();
                        if (prismAction.getActionCategory().equals(CREATE_RESOURCE) && prismAction.name().endsWith(creationScope.name())) {
                            if (prismAction.getScope().equals(responseScope)) {
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

    public HashMultimap<PrismScope, Integer> getResourcesForWhichUserCanAdminister(User user) {
        HashMultimap<PrismScope, Integer> resources = HashMultimap.create();

        List<PrismScope> scopes = Arrays.asList(PrismScope.values());
        for (PrismScope scope : scopes) {
            String scopeReference = scope.name();

            getResources(user, scope, scopes.stream()
                            .filter(parentScope -> parentScope.ordinal() < scope.ordinal())
                            .collect(Collectors.toList()), //
                    advertService.getAdvertTargeterEntities(user, scope), //
                    new ResourceListFilterDTO().withRoleCategory(ADMINISTRATOR).withActionIds(Arrays.asList((PrismAction.valueOf(scopeReference + "_VIEW_EDIT")))) //
                            .withActionEnhancements(actionService.getAdministratorActionEnhancements(scope)), //
                    Projections.projectionList() //
                            .add(Projections.groupProperty("resource.id").as("id")),
                    ResourceOpportunityCategoryDTO.class).forEach(resource -> {
                resources.put(scope, resource.getId());
            });
        }

        return resources;
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
            setResourceOpportunityCategories(resource, resourceDTO.getOpportunityCategories().stream().map(Enum::name).collect(joining("|")));
        }

        setResourceConditions(resource, resourceDTO.getConditions());
    }

    public void setResourceConditions(ResourceParent resource, List<ResourceConditionDTO> resourceConditions) {
        resource.getResourceConditions().clear();
        entityService.flush();

        if (isEmpty(resourceConditions)) {
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
                    resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_APPLICATION).withInternalMode(true).withExternalMode(true));
                    break;
                case PROJECT:
                    resourceConditions.add(new ResourceConditionDTO().withActionCondition(ACCEPT_APPLICATION).withInternalMode(true).withExternalMode(true));
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

    public void updateResource(ResourceParent resource, ResourceParentDTO resourceDTO) {
        resource.setImportedCode(resourceDTO.getImportedCode());
        resource.setName(resourceDTO.getName());

        Advert advert = resource.getAdvert();
        advertService.updateAdvert(advert, resourceDTO);

        advertService.updateAdvertVisibility(advert, resourceDTO);

        List<ResourceConditionDTO> resourceConditions = resourceDTO.getConditions();
        setResourceConditions(resource, resourceConditions == null ? Lists.newArrayList() : resourceConditions);
    }

    public void updateOpportunity(PrismScope resourceScope, Integer resourceId, ResourceOpportunityDTO resourceDTO) {
        ResourceOpportunity resource = (ResourceOpportunity) getById(resourceScope, resourceId);
        updateResource(resource, resourceDTO);

        resource.setAvailableDate(resourceDTO.getAvailableDate());

        Integer durationMinimum = resourceDTO.getDurationMinimum();
        Integer durationMaximum = resourceDTO.getDurationMaximum();
        if (!(durationMinimum == null && durationMaximum == null)) {
            durationMinimum = durationMinimum == null ? durationMaximum : durationMinimum;
            durationMaximum = durationMaximum == null ? durationMinimum : durationMaximum;

            resource.setDurationMinimum(durationMinimum);
            resource.setDurationMaximum(durationMaximum);
        }

        setResourceOpportunityType(resource, resourceDTO.getOpportunityType());
        advertService.updateFinancialDetail(resource.getAdvert(), resourceDTO.getFinancialDetail());
        List<PrismStudyOption> studyOptions = resourceDTO.getStudyOptions();
        setStudyOptions(resource, studyOptions == null ? newArrayList() : studyOptions);
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

    public ResourceFlatToNestedDTO getResourceWithParentResources(Resource resource, List<PrismScope> parentScopes) {
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

    public ResourceParent getActiveResourceByName(Resource parentResource, PrismScope resourceScope, String name) {
        Class<? extends Resource> resourceClass = resourceScope.getResourceClass();
        if (ResourceParent.class.isAssignableFrom(resourceClass)) {
            return resourceDAO.getActiveResourceByName(parentResource, resourceScope, name);
        }
        return null;
    }

    public Set<ResourceOpportunityCategoryDTO> getResources(User user, PrismScope scope, ResourceListFilterDTO filter) {
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);
        List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(user, scope);
        return getResources(user, scope, parentScopes, targeterEntities, filter, null);
    }

    public Set<ResourceOpportunityCategoryDTO> getResources(User user, PrismScope scope, List<PrismScope> parentScopes) {
        return getResources(user, scope, parentScopes, emptyList(), new ResourceListFilterDTO(), null);
    }

    public Set<ResourceOpportunityCategoryDTO> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, List<Integer> targeterEntities) {
        return getResources(user, scope, parentScopes, targeterEntities, new ResourceListFilterDTO(), null);
    }

    public Set<ResourceOpportunityCategoryDTO> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, List<Integer> targeterEntities,
                                                            ResourceListFilterDTO filter) {
        filter = resourceListFilterService.saveOrGetByUserAndScope(user, scope, filter);
        return getResources(user, scope, parentScopes, targeterEntities, filter, getFilterConditions(scope, filter));
    }

    public <T extends EntityOpportunityCategoryDTO<?>> Set<T> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, List<Integer> targeterEntities,
                                                                           ProjectionList columns,
                                                                           Class<T> responseClass) {
        return getResources(user, scope, parentScopes, targeterEntities, new ResourceListFilterDTO(), columns, responseClass);
    }

    public <T extends EntityOpportunityCategoryDTO<?>> Set<T> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, List<Integer> targeterEntities,
                                                                           ResourceListFilterDTO filter, ProjectionList columns,
                                                                           Class<T> responseClass) {
        return getResources(user, scope, parentScopes, targeterEntities, filter, columns, getFilterConditions(scope, filter), responseClass);
    }

    public List<ResourceSimpleDTO> getResources(Resource enclosingResource, PrismScope resourceScope, Optional<String> query) {
        return resourceDAO.getResources(enclosingResource, resourceScope, query);
    }

    public User joinResource(ResourceParent resource, UserDTO userDTO, PrismRoleContext roleContext) {
        User user = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        joinResource(resource, user, roleContext, null);
        return user;
    }

    public void joinResource(ResourceParent resource, User user, PrismRoleContext roleContext) {
        joinResource(resource, user, roleContext, true);
    }

    public void joinResource(ResourceCreationDTO resource, User user, PrismRoleContext roleContext) {
        joinResource((ResourceParent) getById(resource.getScope(), resource.getId()), user, roleContext, true);
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

    public <T extends Resource> void validateViewResource(T resource) {
        User user = userService.getCurrentUser();
        Action action = actionService.getViewEditAction(resource);
        if (action == null || !actionService.checkActionVisible(resource, action, user)) {
            throw new PrismForbiddenException("User cannot view or edit the given resource");
        }
    }

    public HashMultimap<PrismScope, Integer> getEnclosedResources(Resource resource) {
        Integer resourceId = resource.getId();
        PrismScope resourceScope = resource.getResourceScope();

        HashMultimap<PrismScope, Integer> resources = HashMultimap.create();
        resources.put(resourceScope, resourceId);

        Arrays.stream(PrismScope.values()).filter(scope -> scope.ordinal() > resourceScope.ordinal()).forEach(enclosedScope -> {
            List<Integer> enclosedResources = resourceDAO.getEnclosedResources(resourceScope, resourceId, enclosedScope);
            if (CollectionUtils.isNotEmpty(enclosedResources)) {
                resources.putAll(enclosedScope, enclosedResources);
            }
        });

        return resources;
    }

    public List<Integer> getResourcesByTheme(PrismScope resourceScope, String theme) {
        return resourceDAO.getResourcesByTheme(resourceScope, theme);
    }

    public List<Integer> getResourcesByLocation(PrismScope resourceScope, String location) {
        return resourceDAO.getResourcesByLocation(resourceScope, location);
    }

    public List<Integer> getResourcesForStateActionPendingAssignment(User user, PrismScope resourceScope, List<Comment> replicableSequenceComments) {
        List<PrismAction> actions = replicableSequenceComments.stream().map(rcs -> rcs.getAction().getId()).collect(Collectors.toList());
        List<Integer> replicableSequenceResources = getResources(user, resourceScope, new ResourceListFilterDTO().withActionIds(actions)).stream()
                .map(replicableSequenceResource -> replicableSequenceResource.getId()).collect(Collectors.toList());
        replicableSequenceResources.removeAll(resourceDAO.getResourcesWithStateActionsPending(resourceScope, actions));
        return replicableSequenceResources;
    }

    private Set<ResourceOpportunityCategoryDTO> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, List<Integer> targeterEntities,
                                                             ResourceListFilterDTO filter, Junction conditions) {
        return getResources(user, scope, parentScopes, targeterEntities, filter, //
                Projections.projectionList() //
                        .add(Projections.groupProperty("resource.id").as("id")) //
                        .add(Projections.max("stateAction.raisesUrgentFlag").as("prioritize")) //
                        .add(Projections.property("resource.opportunityCategories").as("opportunityCategories")) //
                        .add(Projections.property("resource.sequenceIdentifier").as("sequenceIdentifier")), //
                conditions, ResourceOpportunityCategoryDTO.class);
    }

    private <T extends EntityOpportunityCategoryDTO<?>> Set<T> getResources(User user, PrismScope scope, List<PrismScope> parentScopes, List<Integer> targeterEntities,
                                                                            ResourceListFilterDTO filter, ProjectionList columns, Junction conditions, Class<T> responseClass) {
        Set<T> resources = Sets.newTreeSet();
        DateTime baseline = DateTime.now().minusDays(1);

        Boolean asPartner = responseClass.equals(ResourceOpportunityCategoryDTO.class) ? false : null;
        addResources(resourceDAO.getResources(user, scope, filter, columns, conditions, responseClass, baseline), resources, asPartner);

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScope : parentScopes) {
                addResources(resourceDAO.getResources(user, scope, parentScope, filter, columns, conditions, responseClass, baseline), resources, asPartner);
            }

            if (isNotEmpty(targeterEntities)) {
                for (PrismScope targeterScope : organizationScopes) {
                    for (PrismScope targetScope : organizationScopes) {
                        addResources(resourceDAO.getResources(user, scope, targeterScope, targetScope, targeterEntities, filter, columns, conditions, responseClass, baseline),
                                resources, asPartner == null ? null : true);
                    }
                }
            }
        }

        return resources;
    }

    private <T> void addResources(List<T> resources, Set<T> resourcesFiltered, Boolean asPartner) {
        boolean processOnlyAsPartner = isTrue(asPartner);
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
        setResourceOpportunityCategories(resourceOpportunity, opportunityCategory);

        for (PrismScope scope : new PrismScope[]{DEPARTMENT, INSTITUTION}) {
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
        Advert advert = resource.getAdvert();
        advert.setOpportunityCategories(opportunityCategories);
        applicationService.updateApplicationOpportunityCategories(advert);
    }

    private void joinResource(ResourceParent resource, User user, PrismRoleContext roleContext, Boolean requested) {
        User currentUser = userService.getCurrentUser();
        Action viewEditAction = actionService.getViewEditAction(resource);
        boolean canViewEdit = viewEditAction != null && actionService.checkActionExecutable(resource, viewEditAction, currentUser);

        Role role = null;
        String resourceName = resource.getResourceScope().name();

        if (roleContext.equals(STUDENT) && !roleService.hasUserRole(resource, user, PrismRole.valueOf(resourceName + "_STUDENT"))) {
            role = roleService.getById(PrismRole.valueOf(resourceName + "_STUDENT" + (canViewEdit ? "" : "_UNVERIFIED")));
        } else if (!roleService.hasUserRole(resource, user, PrismRoleGroup.valueOf(resourceName + "_STAFF_GROUP"))) {
            role = roleService.getById(PrismRole.valueOf(resourceName + "_VIEWER" + (canViewEdit ? "" : "_UNVERIFIED")));
        }

        boolean newRoleCreated = false;
        if (role != null) {
            UserRole transientRole = new UserRole().withResource(resource).withUser(user).withRole(role).withRequested(requested).withAssignedTimestamp(now());
            UserRole persistentRole = entityService.getDuplicateEntity(transientRole);
            if (persistentRole == null) {
                entityService.save(transientRole);
                newRoleCreated = true;
            }
        }

        if (role != null) {
            if (canViewEdit) {
                PrismState transitionState = viewEditAction.getId().name().endsWith("_COMPLETE") ? PrismState.valueOf(resource.getResourceScope().name() + "_UNSUBMITTED") : null;
                executeUpdate(resource, currentUser, PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_USER_ROLE"), transitionState,
                        new CommentAssignedUser().withUser(user).withRole(role).withRoleTransitionType(CREATE));
            } else if (newRoleCreated) {
                userService.getResourceUsers(resource, PrismRole.valueOf(resourceName + "_ADMINISTRATOR")).forEach(admin -> {
                    notificationService.sendJoinRequest(user, admin, resource);
                });
            }
        }
    }

    private boolean validateResourceRelationCreation(ResourceRelationCreationDTO resourceRelationDTO) {
        List<PrismScope> scopes = resourceRelationDTO.getResources().stream().map(r -> r.getScope()).collect(toList());
        for (PrismResourceRelation relation : resourceRelationDTO.getContext().getRelations()) {
            if (relation.stream().map(creation -> creation.getScope()).collect(Collectors.toList()).containsAll(scopes)) {
                return true;
            }
        }
        return false;
    }

}
