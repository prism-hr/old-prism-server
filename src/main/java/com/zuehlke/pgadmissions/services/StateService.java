package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
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
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation.NextStateRepresentation;

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
		return (StateDurationConfiguration) customizationService.getConfiguration(PrismConfiguration.STATE_DURATION, resource, user, definition);
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
		stateDAO.deleteObseleteStateDurations();
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

	public StateTransition executeStateTransition(Resource resource, Action action, Comment comment) throws DeduplicationException, InstantiationException,
	        IllegalAccessException, BeansException, WorkflowEngineException, IOException, IntegrationException {
		comment.setResource(resource);

		resourceService.persistResource(resource, comment);
		commentService.persistComment(resource, comment);

		resourceService.preProcessResource(resource, comment);
		commentService.preProcessComment(resource, comment);

		State state = resource.getState();
		StateTransition stateTransition = getStateTransition(resource, action, comment);

		if (stateTransition == null && comment.isDelegateComment()) {
			commentService.recordDelegatedStateTransition(comment, state);
			roleService.executeDelegatedRoleTransitions(resource, comment);
		} else {
			State transitionState = stateTransition.getTransitionState();
			transitionState = transitionState == null ? state : transitionState;
			state = state == null ? transitionState : state;

			Set<State> stateTerminations = getStateTerminations(resource, action, stateTransition);
			commentService.recordStateTransition(comment, state, transitionState, stateTerminations);
			resourceService.recordStateTransition(resource, comment, state, transitionState);

			commentService.processComment(comment);
			resourceService.processResource(resource, comment);

			roleService.executeRoleTransitions(resource, comment, stateTransition);

			if (stateTransition.hasPropagatedActions()) {
				getOrCreateStateTransitionPending(resource, stateTransition);
			}

			notificationService.sendWorkflowNotifications(resource, comment);
		}

		commentService.postProcessComment(comment);
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

	public <T extends Resource> void executeDeferredStateTransition(Class<T> resourceClass, Integer resourceId, PrismAction actionId)
	        throws DeduplicationException, InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException,
	        IntegrationException {
		Resource resource = resourceService.getById(resourceClass, resourceId);
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
		List<PrismState> recommendations = Lists.newLinkedList();
		String recommendationTokens = stateDAO.getRecommendedNextStates(resource);
		if (recommendationTokens != null) {
			for (String recommendationToken : recommendationTokens.split("\\|")) {
				recommendations.add(PrismState.valueOf(recommendationToken));
			}
			return recommendations;
		}
		return null;
	}

	public List<PrismStateGroup> getSecondaryResourceStateGroups(Resource resource) {
		return getSecondaryResourceStateGroups(resource.getResourceScope(), resource.getId());
	}

	public List<PrismStateGroup> getSecondaryResourceStateGroups(PrismScope resourceScope, Integer resourceId) {
		return stateDAO.getSecondaryResourceStateGroups(resourceScope, resourceId);
	}

	public List<NextStateRepresentation> getSelectableTransitionStates(State state, boolean importedResource) {
		return stateDAO.getSelectableTransitionStates(state, importedResource);
	}

	public List<NextStateRepresentation> getSelectableTransitionStates(State state, PrismAction actionId, boolean importedResource) {
		return stateDAO.getSelectableTransitionStates(state, actionId, importedResource);
	}

	private StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
		Resource operative = resourceService.getOperativeResource(resource, action);
		List<StateTransition> potentialStateTransitions = getPotentialStateTransitions(operative, action);
		if (potentialStateTransitions.size() > 1) {
			return applicationContext.getBean(potentialStateTransitions.get(0).getStateTransitionEvaluation().getId().getResolver()).resolve(operative, comment);
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

	private void getOrCreateStateTransitionPending(Resource resource, StateTransition stateTransition) {
		StateTransitionPending transientTransitionPending = new StateTransitionPending().withResource(resource).withStateTransition(stateTransition);
		entityService.getOrCreate(transientTransitionPending);
	}

	private List<StateTransition> getPotentialStateTransitions(Resource resource, Action action) {
		return stateDAO.getPotentialStateTransitions(resource, action);
	}

}
