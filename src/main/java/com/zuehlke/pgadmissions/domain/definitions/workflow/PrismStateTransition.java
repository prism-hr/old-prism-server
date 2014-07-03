package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class PrismStateTransition {

    private PrismState transitionState;

    private PrismAction transitionAction;

    private PrismTransitionEvaluation transitionEvaluation;

    private List<PrismRoleTransition> roleTransitions = Lists.newArrayList();

    private List<PrismAction> propagatedActions = Lists.newArrayList();

    public PrismState getTransitionState() {
        return transitionState;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public PrismTransitionEvaluation getTransitionEvaluation() {
        return transitionEvaluation;
    }

    public List<PrismRoleTransition> getRoleTransitions() {
        return roleTransitions;
    }

    public List<PrismAction> getPropagatedActions() {
        return propagatedActions;
    }

    public PrismStateTransition withTransitionState(PrismState transitionState) {
        this.transitionState = transitionState;
        return this;
    }

    public PrismStateTransition withTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public PrismStateTransition withTransitionEvaluation(PrismTransitionEvaluation transitionEvaluation) {
        this.transitionEvaluation = transitionEvaluation;
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

    @Override
    public int hashCode() {
        return Objects.hashCode(transitionState, transitionAction, transitionEvaluation, roleTransitions, propagatedActions);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PrismStateTransition other = (PrismStateTransition) obj;
        final List<PrismRoleTransition> otherRoleTransitions = other.getRoleTransitions();
        final List<PrismAction> otherPropagatedActions = other.getPropagatedActions();
        return Objects.equal(transitionState, other.getTransitionState()) && Objects.equal(transitionAction, other.getTransitionAction())
                && Objects.equal(transitionEvaluation, other.getTransitionEvaluation()) && roleTransitions.size() == otherRoleTransitions.size()
                && roleTransitions.containsAll(otherRoleTransitions) && propagatedActions.size() == otherPropagatedActions.size()
                && propagatedActions.containsAll(otherPropagatedActions);
    }
    
}
