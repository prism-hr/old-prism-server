package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class PrismStateTransition {

    private PrismState transitionState;

    private PrismAction transitionAction;

    private PrismStateTransitionEvaluation transitionEvaluation;

    private List<PrismRoleTransition> roleTransitions = Lists.newLinkedList();

    private List<PrismAction> propagatedActions = Lists.newLinkedList();

    private List<PrismStateTermination> stateTerminations = Lists.newLinkedList();

    public PrismState getTransitionState() {
        return transitionState;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public PrismStateTransitionEvaluation getTransitionEvaluation() {
        return transitionEvaluation;
    }

    public List<PrismRoleTransition> getRoleTransitions() {
        return roleTransitions;
    }

    public List<PrismAction> getPropagatedActions() {
        return propagatedActions;
    }

    public List<PrismStateTermination> getStateTerminations() {
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

    public PrismStateTransition withTransitionEvaluation(PrismStateTransitionEvaluation transitionEvaluation) {
        this.transitionEvaluation = transitionEvaluation;
        return this;
    }

    public PrismStateTransition withRoleTransitions(PrismRoleTransition... roleTransitions) {
        this.roleTransitions.addAll(Arrays.asList(roleTransitions));
        return this;
    }

    public PrismStateTransition withRoleTransitions(PrismRoleTransitionGroup... roleTransitionGroups) {
        for (PrismRoleTransitionGroup roleTransitionGroup : roleTransitionGroups) {
            this.roleTransitions.addAll(Lists.newArrayList(roleTransitionGroup.getRoleTransitions()));
        }
        return this;
    }

    public PrismStateTransition withPropagatedActions(PrismAction... propagatedActions) {
        this.propagatedActions.addAll(Arrays.asList(propagatedActions));
        return this;
    }

    public PrismStateTransition withStateTerminations(PrismStateTermination... stateTerminations) {
        this.stateTerminations.addAll(Arrays.asList(stateTerminations));
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transitionState, transitionAction, transitionEvaluation, roleTransitions, propagatedActions, stateTerminations);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        PrismStateTransition other = (PrismStateTransition) object;
        List<PrismRoleTransition> otherRoleTransitions = other.getRoleTransitions();
        List<PrismAction> otherPropagatedActions = other.getPropagatedActions();
        List<PrismStateTermination> otherStateTerminations = other.getStateTerminations();
        return Objects.equal(transitionState, other.getTransitionState()) && Objects.equal(transitionAction, other.getTransitionAction())
                && Objects.equal(transitionEvaluation, other.getTransitionEvaluation()) && roleTransitions.size() == otherRoleTransitions.size()
                && roleTransitions.containsAll(otherRoleTransitions) && propagatedActions.size() == otherPropagatedActions.size()
                && propagatedActions.containsAll(otherPropagatedActions) && stateTerminations.size() == otherStateTerminations.size()
                && stateTerminations.containsAll(otherStateTerminations);
    }

}
