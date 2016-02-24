package uk.co.alumeni.prism.domain.definitions.workflow;

import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.lang.WordUtils.capitalize;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.invokeMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public class PrismStateAction {

    private PrismAction action;

    private Boolean raisesUrgentFlag = false;

    private Boolean replicableSequenceStart = false;

    private PrismActionCondition actionCondition;

    private PrismActionEnhancement actionEnhancement;

    private PrismNotificationDefinition notification;

    private Set<PrismStateActionAssignment> assignments = newHashSet();

    private Set<PrismStateActionNotification> notifications = newHashSet();

    private Set<PrismStateTransition> transitions = newHashSet();

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

    public Set<PrismStateActionAssignment> getAssignments() {
        return assignments;
    }

    public Set<PrismStateActionNotification> getNotifications() {
        return notifications;
    }

    public Set<PrismStateTransition> getTransitions() {
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
        PrismStateActionAssignment newAssignment = new PrismStateActionAssignment().withRole(role);
        return addRecipientAssignment(newAssignment, recipient, "recipient");
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

    public PrismStateAction withPartnerAssignment(PrismRole role, PrismRole recipient) {
        PrismStateActionAssignment newAssignment = new PrismStateActionAssignment().withRole(role).withExternalMode();
        return addRecipientAssignment(newAssignment, recipient, "recipient");
    }

    public PrismStateAction withPartnerAssignments(PrismRole... roles) {
        for (PrismRole role : roles) {
            this.assignments.add(new PrismStateActionAssignment().withRole(role).withExternalMode());
        }
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

    public PrismStateAction withPartnerRecipientAssignments(PrismRoleGroup roles, PrismRoleGroup recipients) {
        for (PrismRole role : roles.getRoles()) {
            PrismStateActionAssignment newAssignment = new PrismStateActionAssignment().withRole(role);
            for (PrismRole recipient : recipients.getRoles()) {
                addRecipientAssignment(newAssignment, recipient, "partnerRecipient");
            }
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

    private PrismStateAction addRecipientAssignment(PrismStateActionAssignment newAssignment, PrismRole recipient, String recipientProperty) {
        for (PrismStateActionAssignment assignment : this.assignments) {
            if (assignment.equals(newAssignment)) {
                assignment.addRecipient(recipient);
                return this;
            }
        }
        this.assignments.add((PrismStateActionAssignment) invokeMethod(newAssignment, "add" + capitalize(recipientProperty), recipient));
        return this;
    }

}
