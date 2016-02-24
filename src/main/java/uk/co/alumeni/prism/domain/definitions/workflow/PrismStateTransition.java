package uk.co.alumeni.prism.domain.definitions.workflow;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;

import java.util.Set;

import com.google.common.base.Objects;

public class PrismStateTransition {

    private PrismState transitionState;

    private PrismAction transitionAction;

    private Boolean replicableSequenceClose = false;

    private Boolean replicableSequenceFilterTheme = false;

    private Boolean replicableSequenceFilterSecondaryTheme = false;

    private Boolean replicableSequenceFilterLocation = false;

    private Boolean replicableSequenceFilterSecondaryLocation = false;

    private PrismStateTransitionEvaluation transitionEvaluation;

    private Set<PrismRoleTransition> roleTransitions = newHashSet();

    private Set<PrismAction> propagatedActions = newHashSet();

    private Set<PrismStateTermination> stateTerminations = newHashSet();

    public PrismState getTransitionState() {
        return transitionState;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public Boolean getReplicableSequenceClose() {
        return replicableSequenceClose;
    }

    public Boolean getReplicableSequenceFilterTheme() {
        return replicableSequenceFilterTheme;
    }

    public Boolean getReplicableSequenceFilterSecondaryTheme() {
        return replicableSequenceFilterSecondaryTheme;
    }

    public Boolean getReplicableSequenceFilterLocation() {
        return replicableSequenceFilterLocation;
    }

    public Boolean getReplicableSequenceFilterSecondaryLocation() {
        return replicableSequenceFilterSecondaryLocation;
    }

    public PrismStateTransitionEvaluation getTransitionEvaluation() {
        return transitionEvaluation;
    }

    public Set<PrismRoleTransition> getRoleTransitions() {
        return roleTransitions;
    }

    public Set<PrismAction> getPropagatedActions() {
        return propagatedActions;
    }

    public Set<PrismStateTermination> getStateTerminations() {
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

    public PrismStateTransition withReplicableSequenceClose() {
        this.replicableSequenceClose = true;
        return this;
    }

    public PrismStateTransition withReplicableSequenceClose(Boolean replicableSequenceClose) {
        this.replicableSequenceClose = replicableSequenceClose;
        return this;
    }

    public PrismStateTransition withReplicableSequenceFilterTheme() {
        this.replicableSequenceFilterTheme = true;
        return this;
    }

    public PrismStateTransition withReplicableSequenceFilterTheme(Boolean replicableSequenceFilterTheme) {
        this.replicableSequenceFilterTheme = replicableSequenceFilterTheme;
        return this;
    }

    public PrismStateTransition withReplicableSequenceFilterSecondaryTheme() {
        this.replicableSequenceFilterSecondaryTheme = true;
        return this;
    }

    public PrismStateTransition withReplicableSequenceFilterSecondaryTheme(Boolean replicableSequenceFilterSecondaryTheme) {
        this.replicableSequenceFilterSecondaryTheme = replicableSequenceFilterSecondaryTheme;
        return this;
    }

    public PrismStateTransition withReplicableSequenceFilterLocation() {
        this.replicableSequenceFilterLocation = true;
        return this;
    }

    public PrismStateTransition withReplicableSequenceFilterLocation(Boolean replicableSequenceFilterLocation) {
        this.replicableSequenceFilterLocation = replicableSequenceFilterLocation;
        return this;
    }

    public PrismStateTransition withReplicableSequenceFilterSecondaryLocation() {
        this.replicableSequenceFilterSecondaryLocation = true;
        return this;
    }

    public PrismStateTransition withReplicableSequenceFilterSecondaryLocation(Boolean replicableSequenceFilterSecondaryLocation) {
        this.replicableSequenceFilterSecondaryLocation = replicableSequenceFilterSecondaryLocation;
        return this;
    }

    public PrismStateTransition withStateTransitionEvaluation(PrismStateTransitionEvaluation transitionEvaluation) {
        this.transitionEvaluation = transitionEvaluation;
        return this;
    }

    public PrismStateTransition withRoleTransitions(PrismRoleTransition... roleTransitions) {
        this.roleTransitions.addAll(asList(roleTransitions));
        return this;
    }

    public PrismStateTransition withRoleTransitions(PrismRoleTransitionGroup... roleTransitionGroups) {
        for (PrismRoleTransitionGroup roleTransitionGroup : roleTransitionGroups) {
            this.roleTransitions.addAll(newArrayList(roleTransitionGroup.getRoleTransitions()));
        }
        return this;
    }

    public PrismStateTransition withPropagatedActions(PrismAction... propagatedActions) {
        this.propagatedActions.addAll(asList(propagatedActions));
        return this;
    }

    public PrismStateTransition withStateTerminations(PrismStateTermination... stateTerminations) {
        this.stateTerminations.addAll(asList(stateTerminations));
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transitionState, transitionAction, transitionEvaluation);
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
        return equal(transitionState, other.getTransitionState()) && equal(transitionAction, other.getTransitionAction())
                && equal(transitionEvaluation, other.getTransitionEvaluation());
    }

}
