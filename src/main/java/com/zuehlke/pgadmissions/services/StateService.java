package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.STATE_DURATION;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationEvaluation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.workflow.StateActionNotification;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.domain.workflow.StateTermination;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionPending;
import com.zuehlke.pgadmissions.dto.StateSelectableDTO;
import com.zuehlke.pgadmissions.dto.StateTransitionDTO;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Service
@Transactional
public class StateService {

    @Inject
    private StateDAO stateDAO;

    @Inject
    private ActionService actionService;

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
    private SystemService systemService;

    @Inject
    private ApplicationContext applicationContext;

    public State getById(PrismState id) {
        return entityService.getById(State.class, id);
    }

    public StateGroup getStateGroupById(PrismStateGroup stateGroupId) {
        return entityService.getById(StateGroup.class, stateGroupId);
    }

    public StateDurationDefinition getStateDurationDefinitionById(PrismStateDurationDefinition stateDurationDefinitionId) {
        return entityService.getById(StateDurationDefinition.class, stateDurationDefinitionId);
    }

    public StateTransitionEvaluation getStateTransitionEvaluationById(PrismStateTransitionEvaluation stateTransitionEvaluationId) {
        return entityService.getById(StateTransitionEvaluation.class, stateTransitionEvaluationId);
    }

    public List<State> getStates() {
        return entityService.list(State.class);
    }

    public List<State> getConfigurableStates() {
        return stateDAO.getConfigurableStates();
    }

    public List<StateGroup> getStateGroups() {
        return entityService.list(StateGroup.class);
    }

    public List<StateTransitionEvaluation> getStateTransitionEvaluations() {
        return entityService.list(StateTransitionEvaluation.class);
    }

    public StateDurationConfiguration getStateDurationConfiguration(Resource resource, User user, StateDurationDefinition definition) {
        return (StateDurationConfiguration) customizationService.getConfiguration(STATE_DURATION, resource, definition);
    }

    public void deleteStateActions() {
        entityService.deleteAll(RoleTransition.class);
        entityService.deleteAll(StateTermination.class);
        entityService.deleteAll(StateTransition.class);
        entityService.deleteAll(StateTransitionEvaluation.class);
        entityService.deleteAll(StateActionAssignment.class);
        entityService.deleteAll(StateActionNotification.class);
        entityService.deleteAll(StateAction.class);
    }

    public void deleteObsoleteStateDurations() {
        stateDAO.deleteObsoleteStateDurations();
    }

    public List<StateAction> getStateActions() {
        return entityService.list(StateAction.class);
    }

    public List<State> getOrderedTransitionStates(State state, State... excludedTransitionStates) {
        return stateDAO.getOrderedTransitionStates(state, excludedTransitionStates);
    }

    public List<StateTransitionPendingDTO> getStateTransitionsPending(PrismScope scopeId) {
        return stateDAO.getStateTransitionsPending(scopeId);
    }

    public StateTransition executeStateTransition(Resource resource, Action action, Comment comment) throws Exception {
        return executeStateTransition(resource, action, comment, true);
    }

    public StateTransition executeStateTransition(Resource resource, Action action, Comment comment, boolean notify) throws Exception {
        comment.setResource(resource);
        resourceService.persistResource(resource, comment);
        commentService.persistComment(resource, comment);

        resourceService.preProcessResource(resource, comment);
        State state = resource.getState();
        StateTransition stateTransition = getStateTransition(resource, action, comment);

        State transitionState = stateTransition.getTransitionState();
        transitionState = transitionState == null ? state : transitionState;
        state = state == null ? transitionState : state;

        Set<State> stateTerminations = getStateTerminations(resource, action, stateTransition);
        commentService.recordStateTransition(comment, state, transitionState, stateTerminations);
        resourceService.recordStateTransition(resource, comment, state, transitionState);

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
            notificationService.sendWorkflowNotifications(resource, comment);
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

    public void executeDeferredStateTransition(PrismScope resourceScope, Integer resourceId, PrismAction actionId) throws Exception {
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

    public List<PrismState> getInstitutionStates() {
        return stateDAO.getInstitutionStates();
    }

    public List<PrismState> getActiveInstitutionStates() {
        return stateDAO.getActiveInstitutionStates();
    }

    public List<PrismState> getProgramStates() {
        return stateDAO.getProgramStates();
    }

    public List<PrismState> getActiveProgramStates() {
        return stateDAO.getActiveProgramStates();
    }

    public List<PrismState> getProjectStates() {
        return stateDAO.getProjectStates();
    }

    public List<PrismState> getActiveProjectStates() {
        return stateDAO.getActiveProjectStates();
    }

    public List<State> getCurrentStates(Resource resource) {
        return stateDAO.getCurrentStates(resource);
    }

    public List<PrismState> getRecommendedNextStates(Resource resource) {
        String tokens = stateDAO.getRecommendedNextStates(resource);
        return Lists.newArrayList(Splitter.on("|").omitEmptyStrings().split(Strings.nullToEmpty(tokens))).stream()
                .map(token -> PrismState.valueOf(token))
                .collect(Collectors.toList());
    }

    public List<PrismState> getSecondaryResourceStates(Resource resource) {
        return stateDAO.getSecondaryResourceStates(resource.getResourceScope(), resource.getId());
    }

    public List<PrismState> getSecondaryResourceStates(PrismScope resourceScope, Integer resourceId) {
        return stateDAO.getSecondaryResourceStates(resourceScope, resourceId);
    }

    public List<StateSelectableDTO> getSelectableTransitionStates(State state, PrismAction actionId) {
        return stateDAO.getSelectableTransitionStates(state, actionId);
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
        List<PrismState> states = stateDAO.getHiddenStates();
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

    private StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
        Resource operativeResource = resourceService.getOperativeResource(resource, action);

        List<StateTransition> potentialStateTransitions;
        if (BooleanUtils.isTrue(action.getSystemInvocationOnly())) {
            potentialStateTransitions = stateDAO.getPotentialStateTransitions(operativeResource, action);
        } else {
            potentialStateTransitions = stateDAO.getPotentialUserStateTransitions(operativeResource, action);
        }

        return resolveStateTransition(resource, comment, potentialStateTransitions);
    }

    private List<StateTransition> getSecondaryStateTransitions(Resource resource, Action action, Comment comment) {
        Resource operativeResource = resourceService.getOperativeResource(resource, action);

        List<StateTransition> stateTransitions = Lists.newArrayList();
        List<PrismState> states = getSecondaryResourceStates(operativeResource);
        for (PrismState state : states) {
            stateTransitions.add(stateDAO.getSecondaryStateTransition(operativeResource, state, action));
        }

        return stateTransitions;
    }

    private StateTransition resolveStateTransition(Resource resource, Comment comment, List<StateTransition> potentialStateTransitions) {
        if (potentialStateTransitions.size() > 1) {
            Class<? extends StateTransitionResolver> resolver = potentialStateTransitions.get(0).getStateTransitionEvaluation().getId().getResolver();
            return applicationContext.getBean(resolver).resolve(resource, comment);
        }
        return potentialStateTransitions.isEmpty() ? null : potentialStateTransitions.get(0);
    }

    private Set<State> getStateTerminations(Resource resource, Action action, StateTransition stateTransition) {
        Resource operative = resourceService.getOperativeResource(resource, action);
        Set<State> stateTerminations = Sets.newHashSet();
        Set<StateTermination> potentialStateTerminations = stateTransition.getStateTerminations();
        for (StateTermination potentialStateTermination : potentialStateTerminations) {
            PrismStateTerminationEvaluation evaluation = potentialStateTermination.getStateTerminationEvaluation();
            if (evaluation == null || applicationContext.getBean(evaluation.getResolver()).resolve(operative)) {
                stateTerminations.add(potentialStateTermination.getTerminationState());
            }
        }
        return stateTerminations;
    }

    private void getOrCreateStateTransitionPending(Resource resource, Action action) {
        StateTransitionPending transientTransitionPending = new StateTransitionPending().withResource(resource).withAction(action);
        entityService.getOrCreate(transientTransitionPending);
    }

}
