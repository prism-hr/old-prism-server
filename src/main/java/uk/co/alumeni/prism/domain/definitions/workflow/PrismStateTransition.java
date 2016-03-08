package uk.co.alumeni.prism.domain.definitions.workflow;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class PrismStateTransition {

    private PrismState transitionState;

    private PrismAction transitionAction;

    private Boolean replicableSequenceClose = false;

    private Boolean replicableSequenceFilterTheme = false;

    private Boolean replicableSequenceFilterSecondaryTheme = false;

    private Boolean replicableSequenceFilterLocation = false;

    private Boolean replicableSequenceFilterSecondaryLocation = false;

    private PrismStateTransitionEvaluation stateTransitionEvaluation;

    private List<PrismStateTransitionNotification> stateTransitionNotifications = Lists.newLinkedList();

    private List<PrismRoleTransition> roleTransitions = Lists.newLinkedList();

    private List<PrismAction> propagatedActions = Lists.newLinkedList();

    private List<PrismStateTermination> stateTerminations = Lists.newLinkedList();

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

    public PrismStateTransitionEvaluation getStateTransitionEvaluation() {
        return stateTransitionEvaluation;
    }

    public List<PrismStateTransitionNotification> getStateTransitionNotifications() {
        return stateTransitionNotifications;
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
        this.stateTransitionEvaluation = transitionEvaluation;
        return this;
    }

    public PrismStateTransition withStateTransitionNotifications(PrismRoleGroup roleGroup, PrismNotificationDefinition stateTransitionNotification) {
        for (PrismRole role : roleGroup.getRoles()) {
            withStateTransitionNotifications(role, stateTransitionNotification);
        }
        return this;
    }

    public PrismStateTransition withStateTransitionNotifications(PrismRole role, PrismNotificationDefinition stateTransitionNotification) {
        stateTransitionNotifications.add(new PrismStateTransitionNotification().withRole(role).withDefinition(stateTransitionNotification));
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
        return Objects.hashCode(transitionState, transitionAction, stateTransitionEvaluation, roleTransitions, propagatedActions, stateTerminations);
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
                && Objects.equal(stateTransitionEvaluation, other.getStateTransitionEvaluation()) && roleTransitions.size() == otherRoleTransitions.size()
                && roleTransitions.containsAll(otherRoleTransitions) && propagatedActions.size() == otherPropagatedActions.size()
                && propagatedActions.containsAll(otherPropagatedActions) && stateTerminations.size() == otherStateTerminations.size()
                && stateTerminations.containsAll(otherStateTerminations);
    }

}
