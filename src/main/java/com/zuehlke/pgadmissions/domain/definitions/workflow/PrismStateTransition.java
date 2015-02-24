package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class PrismStateTransition {

	private PrismState transitionState;

	private PrismAction transitionAction;

	private PrismStateTransitionEvaluation stateTransitionEvaluation;

	private PrismResourceBatch resourceBatchStart;

	private PrismResourceBatch resourceBatchClose;

	private List<PrismRoleTransition> roleTransitions = Lists.newArrayList();

	private List<PrismAction> propagatedActions = Lists.newArrayList();

	private List<PrismStateTermination> stateTerminations = Lists.newArrayList();

	public PrismState getTransitionState() {
		return transitionState;
	}

	public PrismAction getTransitionAction() {
		return transitionAction;
	}

	public PrismStateTransitionEvaluation getStateTransitionEvaluation() {
		return stateTransitionEvaluation;
	}

	public PrismResourceBatch getResourceBatchStart() {
		return resourceBatchStart;
	}

	public PrismResourceBatch getResourceBatchClose() {
		return resourceBatchClose;
	}

	public List<PrismRoleTransition> getRoleTransitions() {
		return roleTransitions;
	}

	public List<PrismAction> getPropagatedActions() {
		return propagatedActions;
	}

	public final List<PrismStateTermination> getStateTerminations() {
		return stateTerminations;
	}

	public PrismStateTransition withTransitionState(PrismState transitionState) {
		this.transitionState = transitionState;
		return this;
	}

	public PrismStateTransition withTransitionAction(PrismAction transitionAction) {
		this.transitionAction = transitionAction;
		return this;
	}

	public PrismStateTransition withStateTransitionEvaluation(PrismStateTransitionEvaluation transitionEvaluation) {
		this.stateTransitionEvaluation = transitionEvaluation;
		return this;
	}

	public PrismStateTransition withResourceBatchStart(PrismResourceBatch resourceBatchStart) {
		this.resourceBatchStart = resourceBatchStart;
		return this;
	}

	public PrismStateTransition withResourceBatchClose(PrismResourceBatch resourceBatchClose) {
		this.resourceBatchClose = resourceBatchClose;
		return this;
	}

	public PrismStateTransition withRoleTransitions(List<PrismRoleTransition> roleTransitions) {
		this.roleTransitions = roleTransitions == null ? this.roleTransitions : roleTransitions;
		return this;
	}

	public PrismStateTransition withPropagatedActions(List<PrismAction> propagatedActions) {
		this.propagatedActions = propagatedActions == null ? this.propagatedActions : propagatedActions;
		return this;
	}

	public PrismStateTransition withStateTerminations(List<PrismStateTermination> stateTerminations) {
		this.stateTerminations = stateTerminations == null ? this.stateTerminations : stateTerminations;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(transitionState, transitionAction, stateTransitionEvaluation, resourceBatchStart, resourceBatchClose, roleTransitions,
		        propagatedActions, stateTerminations);
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		final PrismStateTransition other = (PrismStateTransition) object;
		final List<PrismRoleTransition> otherRoleTransitions = other.getRoleTransitions();
		final List<PrismAction> otherPropagatedActions = other.getPropagatedActions();
		final List<PrismStateTermination> otherStateTerminations = other.getStateTerminations();
		return Objects.equal(transitionState, other.getTransitionState()) && Objects.equal(transitionAction, other.getTransitionAction())
		        && Objects.equal(stateTransitionEvaluation, other.getStateTransitionEvaluation())
		        && Objects.equal(resourceBatchStart, other.getResourceBatchStart())
		        && Objects.equal(resourceBatchClose, other.getResourceBatchClose()) && roleTransitions.size() == otherRoleTransitions.size()
		        && roleTransitions.containsAll(otherRoleTransitions) && propagatedActions.size() == otherPropagatedActions.size()
		        && propagatedActions.containsAll(otherPropagatedActions) && stateTerminations.size() == otherStateTerminations.size()
		        && stateTerminations.containsAll(otherStateTerminations);
	}

}
