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

    private List<PrismStateActionAssignment> assignments = Lists.newLinkedList();

    private List<PrismStateActionNotification> notifications = Lists.newLinkedList();

    private List<PrismStateTransition> transitions = Lists.newLinkedList();

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

    public List<PrismStateActionAssignment> getAssignments() {
        return assignments;
    }

    public List<PrismStateActionNotification> getNotifications() {
        return notifications;
    }

    public List<PrismStateTransition> getTransitions() {
        return transitions;
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

    public PrismStateAction withAssignment(PrismRole role, PrismRole recipient) {
        assignments.add(new PrismStateActionAssignment().withRole(role).withRecipients(recipient));
        return this;
    }

    public PrismStateAction withAssignment(PrismRole role, PrismRoleGroup recipients) {
        for (PrismRole recipient : recipients.getRoles()) {
            withAssignment(role, recipient);
        }
        return this;
    }

    public PrismStateAction withAssignments(PrismRole role, PrismActionEnhancement actionEnhancement) {
        this.assignments.add(new PrismStateActionAssignment().withRole(role).withActionEnhancement(actionEnhancement));
        return this;
    }

    public PrismStateAction withAssignments(PrismRoleGroup roles, PrismActionEnhancement actionEnhancement) {
        for (PrismRole role : roles.getRoles()) {
            this.assignments.add(new PrismStateActionAssignment().withRole(role).withActionEnhancement(actionEnhancement));
        }
        return this;
    }

    public PrismStateAction withAssignments(PrismRole... roles) {
        for (PrismRole role : roles) {
            assignments.add(new PrismStateActionAssignment().withRole(role));
        }
        return this;
    }

    public PrismStateAction withAssignments(PrismRoleGroup roles) {
        withAssignments(roles.getRoles());
        return this;
    }

    public PrismStateAction withAssignments(PrismRoleGroup roles, PrismRole recipient) {
        for (PrismRole role : roles.getRoles()) {
            withAssignment(role, recipient);
        }
        return this;
    }

    public PrismStateAction withAssignments(PrismRoleGroup roles, PrismRoleGroup recipients) {
        for (PrismRole role : roles.getRoles()) {
            withAssignment(role, recipients);
        }
        return this;
    }

    public PrismStateAction withPartnerAssignments(PrismRole... roles) {
        for (PrismRole role : roles) {
            this.assignments.add(new PrismStateActionAssignment().withRole(role).withExternalMode());
        }
        return this;
    }

    public PrismStateAction withPartnerAssignment(PrismRole role, PrismRole recipient) {
        assignments.add(new PrismStateActionAssignment().withRole(role).withExternalMode().withRecipients(recipient));
        return this;
    }

    public PrismStateAction withPartnerAssignment(PrismRole role, PrismRoleGroup recipients) {
        for (PrismRole recipient : recipients.getRoles()) {
            withPartnerAssignment(role, recipient);
        }
        return this;
    }

    public PrismStateAction withPartnerAssignments(PrismRoleGroup roles) {
        withPartnerAssignments(roles.getRoles());
        return this;
    }

    public PrismStateAction withPartnerAssignments(PrismRoleGroup roles, PrismRole recipient) {
        for (PrismRole role : roles.getRoles()) {
            withPartnerAssignment(role, recipient);
        }
        return this;
    }

    public PrismStateAction withPartnerAssignments(PrismRoleGroup roles, PrismRoleGroup recipients) {
        for (PrismRole role : roles.getRoles()) {
            withPartnerAssignment(role, recipients);
        }
        return this;
    }

    public PrismStateAction withPartnerAssignments(PrismRole role, PrismActionEnhancement actionEnhancement) {
        this.assignments.add(new PrismStateActionAssignment().withRole(role).withExternalMode().withActionEnhancement(actionEnhancement));
        return this;
    }

    public PrismStateAction withPartnerAssignments(PrismRoleGroup roles, PrismActionEnhancement actionEnhancement) {
        for (PrismRole role : roles.getRoles()) {
            this.assignments.add(new PrismStateActionAssignment().withRole(role).withExternalMode().withActionEnhancement(actionEnhancement));
        }
        return this;
    }

    public PrismStateAction withNotifications(PrismRoleGroup roleGroup, PrismNotificationDefinition notification) {
        for (PrismRole role : roleGroup.getRoles()) {
            withNotifications(role, notification);
        }
        return this;
    }

    public PrismStateAction withNotifications(PrismRole role, PrismNotificationDefinition notification) {
        notifications.add(new PrismStateActionNotification().withRole(role).withDefinition(notification));
        return this;
    }

    public PrismStateAction withStateTransitions(PrismStateTransition... transitions) {
        this.transitions.addAll(Arrays.asList(transitions));
        return this;
    }

    public PrismStateAction withStateTransitions(PrismStateTransitionGroup stateTransitionGroup) {
        List<PrismStateTransition> transitions = Lists.newLinkedList();
        for (PrismStateTransition stateTransition : stateTransitionGroup.getStateTransitions()) {
            transitions.add(stateTransition);
        }
        this.transitions.addAll(transitions);
        return this;
    }

}
