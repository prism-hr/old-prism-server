package uk.co.alumeni.prism.domain.definitions.workflow;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public class PrismStateAction {

    private PrismAction action;

    private Boolean raisesUrgentFlag = false;

    private Boolean replicableSequenceStart = false;

    private PrismActionCondition actionCondition;

    private PrismActionEnhancement actionEnhancement;

    private PrismNotificationDefinition notification;

    private List<PrismStateActionAssignment> stateActionAssignments = Lists.newLinkedList();

    private List<PrismStateTransition> stateTransitions = Lists.newLinkedList();

    public PrismAction getAction() {
        return action;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public Boolean getReplicableSequenceStart() {
        return replicableSequenceStart;
    }

    public PrismActionCondition getActionCondition() {
        return actionCondition;
    }

    public PrismActionEnhancement getActionEnhancement() {
        return actionEnhancement;
    }

    public PrismNotificationDefinition getNotification() {
        return notification;
    }

    public List<PrismStateActionAssignment> getStateActionAssignments() {
        return stateActionAssignments;
    }

    public List<PrismStateTransition> getStateTransitions() {
        return stateTransitions;
    }

    public PrismStateAction withAction(PrismAction action) {
        this.action = action;
        return this;
    }

    public PrismStateAction withRaisesUrgentFlag() {
        this.raisesUrgentFlag = true;
        return this;
    }

    public PrismStateAction withReplicableSequenceStart() {
        this.replicableSequenceStart = true;
        return this;
    }

    public PrismStateAction withActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
        return this;
    }

    public PrismStateAction withActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
        return this;
    }

    public PrismStateAction withNotification(PrismNotificationDefinition notification) {
        this.notification = notification;
        return this;
    }

    public PrismStateAction withStateActionAssignments(PrismRole role, PrismActionEnhancement actionEnhancement) {
        this.stateActionAssignments.add(new PrismStateActionAssignment().withRole(role).withActionEnhancement(actionEnhancement));
        return this;
    }

    public PrismStateAction withStateActionAssignments(PrismRoleGroup roles, PrismActionEnhancement actionEnhancement) {
        for (PrismRole role : roles.getRoles()) {
            this.stateActionAssignments.add(new PrismStateActionAssignment().withRole(role).withActionEnhancement(actionEnhancement));
        }
        return this;
    }

    public PrismStateAction withStateActionAssignments(PrismRole... roles) {
        for (PrismRole role : roles) {
            stateActionAssignments.add(new PrismStateActionAssignment().withRole(role));
        }
        return this;
    }

    public PrismStateAction withStateActionAssignments(PrismRoleGroup roles) {
        withStateActionAssignments(roles.getRoles());
        return this;
    }

    public PrismStateAction withPartnerStateActionAssignments(PrismRole... roles) {
        for (PrismRole role : roles) {
            this.stateActionAssignments.add(new PrismStateActionAssignment().withRole(role).withExternalMode());
        }
        return this;
    }

    public PrismStateAction withPartnerStateActionAssignments(PrismRoleGroup roles) {
        withPartnerStateActionAssignments(roles.getRoles());
        return this;
    }

    public PrismStateAction withPartnerStateActionAssignments(PrismRole role, PrismActionEnhancement actionEnhancement) {
        this.stateActionAssignments.add(new PrismStateActionAssignment().withRole(role).withExternalMode().withActionEnhancement(actionEnhancement));
        return this;
    }

    public PrismStateAction withPartnerStateActionAssignments(PrismRoleGroup roles, PrismActionEnhancement actionEnhancement) {
        for (PrismRole role : roles.getRoles()) {
            this.stateActionAssignments.add(new PrismStateActionAssignment().withRole(role).withExternalMode().withActionEnhancement(actionEnhancement));
        }
        return this;
    }

    public PrismStateAction withStateTransitions(PrismStateTransition... transitions) {
        this.stateTransitions.addAll(Arrays.asList(transitions));
        return this;
    }

    public PrismStateAction withStateTransitions(PrismStateTransitionGroup stateTransitionGroup) {
        List<PrismStateTransition> transitions = Lists.newLinkedList();
        for (PrismStateTransition stateTransition : stateTransitionGroup.getStateTransitions()) {
            transitions.add(stateTransition);
        }
        this.stateTransitions.addAll(transitions);
        return this;
    }

}
