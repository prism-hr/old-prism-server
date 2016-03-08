package uk.co.alumeni.prism.services;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isFalse;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static uk.co.alumeni.prism.dao.WorkflowDAO.organizationScopes;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration.STATE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.StateDAO;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationEvaluation;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.domain.workflow.RoleTransition;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateAction;
import uk.co.alumeni.prism.domain.workflow.StateActionAssignment;
import uk.co.alumeni.prism.domain.workflow.StateActionNotification;
import uk.co.alumeni.prism.domain.workflow.StateActionPending;
import uk.co.alumeni.prism.domain.workflow.StateActionRecipient;
import uk.co.alumeni.prism.domain.workflow.StateDurationConfiguration;
import uk.co.alumeni.prism.domain.workflow.StateDurationDefinition;
import uk.co.alumeni.prism.domain.workflow.StateGroup;
import uk.co.alumeni.prism.domain.workflow.StateTermination;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.domain.workflow.StateTransitionEvaluation;
import uk.co.alumeni.prism.domain.workflow.StateTransitionPending;
import uk.co.alumeni.prism.dto.StateActionRecipientDTO;
import uk.co.alumeni.prism.dto.StateSelectableDTO;
import uk.co.alumeni.prism.dto.StateTransitionDTO;
import uk.co.alumeni.prism.dto.StateTransitionPendingDTO;
import uk.co.alumeni.prism.exceptions.WorkflowEngineException;
import uk.co.alumeni.prism.rest.dto.StateActionPendingDTO;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;
import uk.co.alumeni.prism.utils.PrismJsonMappingUtils;
import uk.co.alumeni.prism.workflow.resolvers.state.termination.StateTerminationResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.selection.StateTransitionSelectionResolver;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Service
@Transactional
public class StateService {

    @Inject
    private StateDAO stateDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private CommentService commentService;

    @Inject
    private CustomizationService customizationService;

    @Inject
    private EntityService entityService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private SystemService systemService;

    @Inject
    private UserService userService;

    @Inject
    private PrismJsonMappingUtils prismJsonMappingUtils;

    @Inject
    private ApplicationContext applicationContext;

    public State getById(PrismState id) {
        return entityService.getById(State.class, id);
    }

    public StateGroup getStateGroupById(PrismStateGroup id) {
        return entityService.getById(StateGroup.class, id);
    }

    public StateDurationDefinition getStateDurationDefinitionById(PrismStateDurationDefinition id) {
        return entityService.getById(StateDurationDefinition.class, id);
    }

    public StateTransitionEvaluation getStateTransitionEvaluationById(PrismStateTransitionEvaluation id) {
        return entityService.getById(StateTransitionEvaluation.class, id);
    }

    public StateActionPending getStateActionPendingById(Integer id) {
        return entityService.getById(StateActionPending.class, id);
    }

    public StateAction getStateAction(State state, Action action) {
        return entityService.getByProperties(StateAction.class, ImmutableMap.of("state", state, "action", action));
    }

    public List<State> getStates() {
        return entityService.getAll(State.class);
    }

    public List<State> getConfigurableStates() {
        return stateDAO.getConfigurableStates();
    }

    public List<StateGroup> getStateGroups() {
        return entityService.getAll(StateGroup.class);
    }

    public List<StateTransitionEvaluation> getStateTransitionEvaluations() {
        return entityService.getAll(StateTransitionEvaluation.class);
    }

    public List<Integer> getStateActionPendings() {
        return stateDAO.getStateActionPendings();
    }

    public StateDurationConfiguration getStateDurationConfiguration(Resource resource, StateDurationDefinition definition) {
        return (StateDurationConfiguration) customizationService.getConfiguration(STATE_DURATION, resource, definition);
    }

    public void deleteStateActions() {
        entityService.deleteAll(RoleTransition.class);
        entityService.deleteAll(StateTermination.class);
        entityService.deleteAll(StateTransition.class);
        entityService.deleteAll(StateTransitionEvaluation.class);
        entityService.deleteAll(StateActionRecipient.class);
        entityService.deleteAll(StateActionAssignment.class);
        entityService.deleteAll(StateActionNotification.class);
        entityService.deleteAll(StateAction.class);
    }

    public void deleteObsoleteStateDurations() {
        stateDAO.deleteObsoleteStateDurations();
    }

    public List<StateAction> getStateActions() {
        return entityService.getAll(StateAction.class);
    }

    public List<State> getOrderedTransitionStates(State state, State... excludedTransitionStates) {
        return stateDAO.getOrderedTransitionStates(state, excludedTransitionStates);
    }

    public List<StateTransitionPendingDTO> getStateTransitionsPending(PrismScope scopeId) {
        return stateDAO.getStateTransitionsPending(scopeId);
    }

    public StateTransition executeStateTransition(Resource resource, Action action, Comment comment) {
        return executeStateTransition(resource, action, comment, true);
    }

    public StateTransition executeStateTransition(Resource resource, Action action, Comment comment, boolean notify) {
        comment.setResource(resource);
        resourceService.persistResource(resource, comment);
        commentService.persistComment(resource, comment);

        resourceService.preProcessResource(resource, comment);
        StateTransition stateTransition = getStateTransition(resource, action, comment);

        State state = resource.getState();
        State transitionState = stateTransition.getTransitionState();
        transitionState = transitionState == null ? state : transitionState;
        state = state == null ? transitionState : state;

        Set<UserNotificationDefinitionDTO> updates = notificationService.getIndividualUpdateDefinitions(resource, stateTransition);

        Set<State> stateTerminations = getStateTerminations(resource, action, stateTransition);
        commentService.recordStateTransition(comment, state, transitionState, stateTerminations);
        resourceService.recordStateTransition(resource, comment, state, transitionState);
        advertService.recordPartnershipStateTransition(resource, comment);

        resourceService.processResource(resource, comment);
        roleService.executeRoleTransitions(resource, comment, stateTransition);

        List<StateTransition> secondaryStateTransitions = getSecondaryStateTransitions(resource, action, comment);
        for (StateTransition secondaryStateTransition : secondaryStateTransitions) {
            roleService.executeRoleTransitions(resource, comment, secondaryStateTransition);
        }

        for (Action propagatedAction : stateTransition.getPropagatedActions()) {
            getOrCreateStateTransitionPending(resource, propagatedAction);
        }

        if (notify) {
            notificationService.sendIndividualWorkflowNotifications(resource, comment);
        }

        resourceService.postProcessResource(resource, comment);
        return stateTransition;
    }

    public StateTransition getStateTransition(Resource resource, Action action) {
        return stateDAO.getStateTransition(resource, action);
    }

    public StateTransition getStateTransition(Resource resource, Action action, PrismState prismTransitionState) {
        return stateDAO.getStateTransition(resource, action, prismTransitionState);
    }

    public StateTransition getStateTransition(State state, Action action, State transitionState) {
        return stateDAO.getStateTransition(state, action, transitionState);
    }

    public StateTransition getPredefinedStateTransition(Resource resource, Comment comment) {
        State transitionState = comment.getTransitionState();
        if (transitionState != null) {
            return getStateTransition(resource, comment.getAction(), transitionState.getId());
        }
        throw new WorkflowEngineException("Transition state not defined");
    }

    public StateTransition getPredefinedOrCurrentStateTransition(Resource resource, Comment comment) {
        State transitionState = comment.getTransitionState();
        if (transitionState != null) {
            return getStateTransition(resource, comment.getAction(), transitionState.getId());
        }
        return getStateTransition(resource, comment.getAction(), resource.getState().getId());
    }

    @Transactional(timeout = 600)
    @SuppressWarnings("unchecked")
    public void executeStateActionPending(Integer stateActionPendingId) {
        StateActionPending stateActionPending = getStateActionPendingById(stateActionPendingId);

        User user = stateActionPending.getUser();
        Action action = stateActionPending.getAction();
        Resource resource = stateActionPending.getResource();
        if (actionService.checkActionExecutable(resource, action, user)) {
            Role assignUserRole = stateActionPending.getAssignUserRole();
            String assignUserList = stateActionPending.getAssignUserList();

            Set<User> assignUsers = Sets.newHashSet();
            if (!Strings.isNullOrEmpty(assignUserList)) {
                Set<UserDTO> assignUserDTOs = prismJsonMappingUtils.readCollection(assignUserList, Set.class, UserDTO.class);
                assignUsers = assignUserDTOs.stream().map(userService::getOrCreateUser).collect(Collectors.toSet());
            }

            Comment templateComment = stateActionPending.getTemplateComment();
            if (templateComment == null) {
                roleService.createUserRoles(user, resource, assignUsers, stateActionPending.getAssignUserMessage(), assignUserRole.getId());
            } else {
                actionService.executeUserAction(resource, action, commentService.replicateComment(resource, templateComment));
            }
        }

        entityService.delete(stateActionPending);
    }

    @Transactional(timeout = 600)
    public void executeDeferredStateTransition(PrismScope resourceScope, Integer resourceId, PrismAction actionId) {
        Resource resource = resourceService.getById(resourceScope, resourceId);
        Action action = actionService.getById(actionId);
        Comment comment = new Comment().withResource(resource).withUser(systemService.getSystem().getUser()).withAction(action).withDeclinedResponse(false)
                .withCreatedTimestamp(new DateTime());
        executeStateTransition(resource, action, comment);
    }

    public void deleteStateTransitionPending(Integer stateTransitionPendingId) {
        StateTransitionPending pending = entityService.getById(StateTransitionPending.class, stateTransitionPendingId);
        entityService.delete(pending);
    }

    public List<PrismState> getStatesByStateGroup(PrismStateGroup stateGroupId) {
        return stateDAO.getStatesByStateGroup(stateGroupId);
    }

    public List<PrismState> getResourceStates(Resource resource) {
        return resource.getResourceStates().stream().map(rs -> rs.getState().getId()).collect(toList());
    }

    public List<PrismState> getResourceStates(PrismScope resourceScope) {
        return stateDAO.getResourceStates(resourceScope);
    }

    public List<PrismState> getActiveResourceStates(PrismScope resourceScope) {
        return stateDAO.getActiveResourceStates(resourceScope);
    }

    public List<State> getCurrentStates(Resource resource) {
        return stateDAO.getCurrentStates(resource);
    }

    public List<PrismState> getRecommendedNextStates(Resource resource) {
        String tokens = stateDAO.getRecommendedNextStates(resource);
        return Splitter.on("|").omitEmptyStrings().splitToList(Strings.nullToEmpty(tokens)).stream()
                .map(PrismState::valueOf)
                .collect(Collectors.toList());
    }

    public List<PrismState> getSecondaryResourceStates(Resource resource) {
        return stateDAO.getSecondaryResourceStates(resource);
    }

    public LinkedHashMultimap<Integer, PrismState> getSecondaryResourceStates(PrismScope resourceScope, Collection<Integer> resourceIds) {
        LinkedHashMultimap<Integer, PrismState> secondaryResourceStates = LinkedHashMultimap.create();
        stateDAO.getSecondaryResourceStates(resourceScope, resourceIds).forEach(secondaryResourceState -> {
            secondaryResourceStates.put(secondaryResourceState.getResourceId(), secondaryResourceState.getStateId());
        });
        return secondaryResourceStates;
    }

    public List<StateSelectableDTO> getSelectableTransitionStates(Resource resource, PrismAction actionId) {
        List<StateSelectableDTO> transitionStates = Lists.newLinkedList();
        stateDAO.getSelectableTransitionStates(resource.getState(), actionId).stream().forEach(transitionState -> {
            Class<? extends StateTransitionSelectionResolver> resolver = transitionState.getState().getStateTransitionSelectionResolver();
            if (resolver == null || applicationContext.getBean(resolver).resolve(resource)) {
                transitionStates.add(transitionState);
            }
        });
        return transitionStates;
    }

    public void setRepeatableStateGroups() {
        State currentState = null;
        Action currentAction = null;

        Set<StateGroup> transitionStateGroups = Sets.newHashSet();
        List<StateTransitionDTO> stateTransitions = stateDAO.getStateTransitions();
        for (StateTransitionDTO stateTransition : stateTransitions) {
            State state = stateTransition.getState();
            Action action = stateTransition.getAction();

            boolean stateChanged = !Objects.equal(currentState, state);
            boolean actionChanged = !Objects.equal(currentAction, action);
            if (stateChanged || actionChanged) {
                if (transitionStateGroups.size() > 1 && transitionStateGroups.contains(state.getStateGroup())) {
                    currentState.getStateGroup().setRepeatable(true);
                }

                currentState = stateChanged ? state : currentState;
                currentAction = actionChanged ? action : currentAction;
                transitionStateGroups.clear();
            }

            transitionStateGroups.add(stateTransition.getTransitionState().getStateGroup());
        }
    }

    public void setHiddenStates() {
        List<PrismState> states = Lists.newArrayList();
        getStates().forEach(state -> {
            Set<StateAction> stateActions = state.getStateActions();
            if (stateActions.isEmpty()) {
                states.add(state.getId());
            } else {
                int userActions = 0;
                int userAssignments = 0;
                for (StateAction stateAction : stateActions) {
                    userAssignments = userAssignments + stateAction.getStateActionAssignments().size();
                    if (isFalse(stateAction.getAction().getSystemInvocationOnly())) {
                        userActions++;
                    }
                }

                if (userActions == 0 || userAssignments == 0) {
                    states.add(state.getId());
                }
            }
        });

        if (!states.isEmpty()) {
            stateDAO.setHiddenStates(states);
        }
    }

    public void setParallelizableStates() {
        List<PrismState> states = stateDAO.getParallelizableStates();
        if (!states.isEmpty()) {
            stateDAO.setParallelizableStates(states);
        }
    }

    public PrismState getPreviousPrimaryState(Resource resource, PrismState currentState) {
        return stateDAO.getPreviousPrimaryState(resource, currentState);
    }

    public StateActionPending createStateActionPending(Resource resource, Comment templateComment) {
        StateActionPending stateActionPending = new StateActionPending().withResource(resource).withUser(templateComment.getUser())
                .withAction(templateComment.getAction())
                .withTemplateComment(templateComment);
        entityService.save(stateActionPending);
        return stateActionPending;
    }

    public StateActionPending createStateActionPending(Resource resource, User user, Action action, StateActionPendingDTO stateActionPendingDTO) {
        StateActionPending stateActionPending = new StateActionPending().withResource(resource).withUser(user).withAction(action)
                .withAssignUserRole(roleService.getById(stateActionPendingDTO.getAssignUserRole()))
                .withAssignUserList(prismJsonMappingUtils.writeValue(stateActionPendingDTO.getAssignUserList()))
                .withAssignUserMessage(stateActionPendingDTO.getAssignUserMessage());
        entityService.save(stateActionPending);
        return stateActionPending;
    }

    public List<Integer> getStateActionAssignments(User user, Resource resource, Action action) {
        Integer resourceId = resource.getId();
        PrismScope scope = resource.getResourceScope();
        Set<Integer> stateActionAssignments = Sets.newHashSet();

        stateActionAssignments.addAll(stateDAO.getStateActionAssignments(user, scope, resourceId, action));

        if (!scope.equals(SYSTEM)) {
            List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);
            for (PrismScope parentScope : parentScopes) {
                stateActionAssignments.addAll(stateDAO.getStateActionAssignments(user, scope, parentScope, resourceId, action));
            }

            List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(scope);
            if (isNotEmpty(targeterEntities)) {
                for (PrismScope targeterScope : organizationScopes) {
                    for (PrismScope targetScope : organizationScopes) {
                        stateActionAssignments.addAll(stateDAO.getStateActionAssignments(user, scope, targeterScope, targetScope, targeterEntities, resourceId,
                                action));
                    }
                }
            }
        }

        return newArrayList(stateActionAssignments);
    }

    public List<StateActionRecipientDTO> getStateActionRecipients(List<Integer> stateActionAssignments) {
        return stateDAO.getStateActionRecipients(stateActionAssignments);
    }

    private StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
        Resource operativeResource = resourceService.getOperativeResource(resource, action);

        List<StateTransition> potentialStateTransitions;
        if (isTrue(action.getSystemInvocationOnly())) {
            potentialStateTransitions = stateDAO.getPotentialStateTransitions(operativeResource, action);
        } else {
            potentialStateTransitions = stateDAO.getPotentialUserStateTransitions(operativeResource, action);
        }

        return resolveStateTransition(resource, comment, potentialStateTransitions);
    }

    private List<StateTransition> getSecondaryStateTransitions(Resource resource, Action action, Comment comment) {
        Resource operativeResource = resourceService.getOperativeResource(resource, action);

        return getSecondaryResourceStates(operativeResource).stream()
                .map(state -> stateDAO.getSecondaryStateTransition(operativeResource, state, action))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource> StateTransition resolveStateTransition(T resource, Comment comment, List<StateTransition> potentialStateTransitions) {
        if (potentialStateTransitions.size() > 1) {
            StateTransitionResolver<T> resolver = (StateTransitionResolver<T>) applicationContext.getBean(potentialStateTransitions.get(0)
                    .getStateTransitionEvaluation().getId().getResolver());
            return resolver.resolve(resource, comment);
        }
        return potentialStateTransitions.isEmpty() ? null : potentialStateTransitions.get(0);
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource> Set<State> getStateTerminations(T resource, Action action, StateTransition stateTransition) {
        T operative = resourceService.getOperativeResource(resource, action);
        Set<State> stateTerminations = Sets.newHashSet();
        Set<StateTermination> potentialStateTerminations = stateTransition.getStateTerminations();
        for (StateTermination potentialStateTermination : potentialStateTerminations) {
            PrismStateTerminationEvaluation evaluation = potentialStateTermination.getStateTerminationEvaluation();
            if (evaluation == null) {
                stateTerminations.add(potentialStateTermination.getTerminationState());
            } else {
                StateTerminationResolver<T> resolver = (StateTerminationResolver<T>) applicationContext.getBean(evaluation.getResolver());
                if (resolver.resolve(operative)) {
                    stateTerminations.add(potentialStateTermination.getTerminationState());
                }
            }
        }
        return stateTerminations;
    }

    private void getOrCreateStateTransitionPending(Resource resource, Action action) {
        StateTransitionPending transientTransitionPending = new StateTransitionPending().withResource(resource).withAction(action);
        entityService.getOrCreate(transientTransitionPending);
        entityService.flush();
    }

}
