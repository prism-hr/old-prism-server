package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationEvaluation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
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
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

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

	public StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
		Resource operative = resourceService.getOperativeResource(resource, action);
		List<StateTransition> potentialStateTransitions = getPotentialStateTransitions(operative, action);
		if (potentialStateTransitions.size() > 1) {
			return applicationContext.getBean(potentialStateTransitions.get(0).getStateTransitionEvaluation().getId().getResolver()).resolve(resource, comment);
		}
		return potentialStateTransitions.isEmpty() ? null : potentialStateTransitions.get(0);
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

	public StateTransition getApplicationProcessedOutcome(Resource resource, Comment comment) {
		PrismAction actionId = comment.getAction().getId();
		if (actionId == PrismAction.APPLICATION_ESCALATE) {
			return getApplicationRejectedOutcome(resource, comment);
		} else if (actionId == PrismAction.APPLICATION_WITHDRAW) {
			return getApplicationWithdrawnOutcome(resource, comment);
		} else {
			return getApplicationFinalizedOutcome(resource, comment);
		}
	}

	public StateTransition getApplicationTerminatedOutcome(Resource resource, Comment comment) {
		PrismState transitionStateId = PrismState.APPLICATION_REJECTED_COMPLETED;
		PrismStateGroup stateGroupId = resource.getState().getStateGroup().getId();
		if (stateGroupId == PrismStateGroup.APPLICATION_UNSUBMITTED) {
			transitionStateId = PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED;
		} else if (resource.getInstitution().getUclInstitution()) {
			transitionStateId = PrismState.APPLICATION_REJECTED_PENDING_EXPORT;
		}
		return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
	}

	public StateTransition getApplicationVerifiedOutcome(Resource resource, Comment comment) {
		PrismStateGroup stateGroupId = resource.getState().getStateGroup().getId();
		if (stateGroupId == PrismStateGroup.APPLICATION_VERIFICATION) {
			return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_VERIFICATION_PENDING_COMPLETION);
		} else {
			return stateDAO.getStateTransition(resource, comment.getAction(), null);
		}
	}

	public StateTransition getApplicationVerificationCompletedOutcome(Resource resource, Comment comment) {
		PrismStateGroup stateGroupId = resource.getState().getStateGroup().getId();
		if (stateGroupId == PrismStateGroup.APPLICATION_VERIFICATION) {
			return getPredefinedStateTransition(resource, comment);
		} else {
			return stateDAO.getStateTransition(resource, comment.getAction(), null);
		}
	}

	public StateTransition getApplicationReferencedOutcome(Resource resource, Comment comment) throws DeduplicationException {
		PrismStateGroup stateGroupId = resource.getState().getStateGroup().getId();
		if (stateGroupId == PrismStateGroup.APPLICATION_REFERENCE) {
			PrismState transitionState = PrismState.APPLICATION_REFERENCE;
			if (getApplicationReferencedTermination(resource)) {
				transitionState = PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION;
			}
			return stateDAO.getStateTransition(resource, comment.getAction(), transitionState);
		} else {
			return stateDAO.getStateTransition(resource, comment.getAction(), null);
		}
	}

	public StateTransition getApplicationReferenceCompletedOutcome(Resource resource, Comment comment) {
		PrismStateGroup stateGroupId = resource.getState().getStateGroup().getId();
		if (stateGroupId == PrismStateGroup.APPLICATION_REFERENCE) {
			return getPredefinedStateTransition(resource, comment);
		} else {
			return stateDAO.getStateTransition(resource, comment.getAction(), null);
		}
	}

	public StateTransition getApplicationRecruitedOutcome(Resource resource, Comment comment) {
		Comment recruitedComment = commentService.getEarliestComment((ResourceParent) resource, Application.class,
		        PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);
		State parentResourceTransitionState = recruitedComment.getParentResourceTransitionState();

		if (parentResourceTransitionState == null) {
			return stateDAO.getStateTransition(resource, comment.getAction(), resource.getState().getId());
		} else {
			return stateDAO.getStateTransition(resource, comment.getAction(), recruitedComment.getParentResourceTransitionState().getId());
		}
	}

	public StateTransition getInstitutionApprovedOutcome(Resource resource, Comment comment) {
		return getPredefinedStateTransition(resource, comment);
	}

	public StateTransition getProgramApprovedOutcome(Resource resource, Comment comment) {
		return getPredefinedStateTransition(resource, comment);
	}

	public StateTransition getProjectApprovedOutcome(Resource resource, Comment comment) {
		return getPredefinedStateTransition(resource, comment);
	}

	public StateTransition getProgramImportedOutcome(Resource resource, Comment comment) {
		return getPredefinedStateTransition(resource, comment);
	}

	public StateTransition getInstitutionViewEditOutcome(Resource resource, Comment comment) {
		return getPredefinedOrCurrentStateTransition(resource, comment);
	}

	public StateTransition getProgramViewEditOutcome(Resource resource, Comment comment) {
		return getPredefinedOrCurrentStateTransition(resource, comment);
	}

	public StateTransition getProgramEscalatedOutcome(Resource resource, Comment comment) {
		if (BooleanUtils.isTrue(resource.getProgram().getImported())) {
			return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.PROGRAM_DISABLED_PENDING_REACTIVATION);
		} else {
			return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.PROGRAM_DISABLED_COMPLETED);
		}
	}

	public StateTransition getProjectViewEditOutcome(Resource resource, Comment comment) {
		return getPredefinedOrCurrentStateTransition(resource, comment);
	}

	public Boolean getApplicationReferencedTermination(Resource resource) {
		return roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_REFEREE)).size() == 1;
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

	private Set<State> getStateTerminations(Resource resource, Action action, StateTransition stateTransition) {
		Resource operative = resourceService.getOperativeResource(resource, action);

		Set<State> stateTerminations = Sets.newHashSet();
		Set<StateTermination> potentialStateTerminations = stateTransition.getStateTerminations();
		for (StateTermination potentialStateTermination : potentialStateTerminations) {
			PrismStateTerminationEvaluation evaluation = potentialStateTermination.getStateTerminationEvaluation();
			if (evaluation == null || BooleanUtils.isTrue((Boolean) ReflectionUtils.invokeMethod(this, ReflectionUtils.getMethodName(evaluation), operative))) {
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

	private StateTransition getApplicationRejectedOutcome(Resource resource, Comment comment) {
		if (BooleanUtils.isTrue(resource.getInstitution().getUclInstitution())) {
			return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_REJECTED_PENDING_EXPORT);
		} else {
			return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_REJECTED_COMPLETED);
		}
	}

	private StateTransition getApplicationWithdrawnOutcome(Resource resource, Comment comment) {
		if (BooleanUtils.isTrue(resource.getInstitution().getUclInstitution())) {
			return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT);
		} else {
			return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_WITHDRAWN_COMPLETED);
		}
	}

	private StateTransition getApplicationFinalizedOutcome(Resource resource, Comment comment) {
		if (BooleanUtils.isTrue(resource.getProgram().getImported())) {
			return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.valueOf(resource.getState().getId().name() + "_PENDING_EXPORT"));
		} else {
			return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.valueOf(resource.getState().getId().name() + "_COMPLETED"));
		}
	}

}
