package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class PrismStateTransition {

	private PrismState transitionState;

	private PrismAction transitionAction;

	private PrismStateTransitionEvaluation stateTransitionEvaluation;

	private PrismResourceBatchProcess resourceBatchProcessJoin;

	private PrismResourceBatchProcess resourceBatchProcessExit;

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

	public PrismResourceBatchProcess getResourceBatchProcessJoin() {
		return resourceBatchProcessJoin;
	}

	public PrismResourceBatchProcess getResourceBatchProcessExit() {
		return resourceBatchProcessExit;
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

	public PrismStateTransition withResourceBatchProcessJoin(PrismResourceBatchProcess resourceBatchProcessJoin) {
		this.resourceBatchProcessJoin = resourceBatchProcessJoin;
		return this;
	}

	public PrismStateTransition withResourceBatchProcessExit(PrismResourceBatchProcess resourceBatchProcessExit) {
		this.resourceBatchProcessExit = resourceBatchProcessExit;
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
		return Objects.hashCode(transitionState, transitionAction, stateTransitionEvaluation, resourceBatchProcessJoin, resourceBatchProcessExit,
		        roleTransitions, propagatedActions, stateTerminations);
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
		        && Objects.equal(resourceBatchProcessJoin, other.getResourceBatchProcessJoin())
		        && Objects.equal(resourceBatchProcessExit, other.getResourceBatchProcessExit()) && roleTransitions.size() == otherRoleTransitions.size()
		        && roleTransitions.containsAll(otherRoleTransitions) && propagatedActions.size() == otherPropagatedActions.size()
		        && propagatedActions.containsAll(otherPropagatedActions) && stateTerminations.size() == otherStateTerminations.size()
		        && stateTerminations.containsAll(otherStateTerminations);
	}

}
