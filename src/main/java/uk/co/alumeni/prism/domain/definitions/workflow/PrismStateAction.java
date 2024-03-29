package uk.co.alumeni.prism.domain.definitions.workflow;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.apache.commons.lang.WordUtils.capitalize;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.invokeMethod;

public class PrismStateAction {

    private PrismAction action;

    private Boolean raisesUrgentFlag = false;

    private Boolean replicableSequenceStart = false;

    private PrismActionCondition actionCondition;

    private PrismActionEnhancement actionEnhancement;

    private PrismNotificationDefinition notificationDefinition;

    private Set<PrismStateActionAssignment> stateActionAssignments = newLinkedHashSet();

    private Set<PrismStateTransition> stateTransitions = newLinkedHashSet();

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

    public PrismNotificationDefinition getNotificationDefinition() {
        return notificationDefinition;
    }

    public Set<PrismStateActionAssignment> getStateActionAssignments() {
        return stateActionAssignments;
    }

    public Set<PrismStateTransition> getStateTransitions() {
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

    public PrismStateAction withNotificationDefinition(PrismNotificationDefinition notificationDefinition) {
        this.notificationDefinition = notificationDefinition;
        return this;
    }

    public PrismStateAction withStateActionAssignment(PrismRole role, PrismRole recipient) {
        PrismStateActionAssignment newAssignment = new PrismStateActionAssignment().withRole(role);
        return addRecipientAssignment(newAssignment, recipient, "recipient");
    }

    public PrismStateAction withStateActionAssignment(PrismRole role, PrismRoleGroup recipients) {
        for (PrismRole recipient : recipients.getRoles()) {
            withStateActionAssignment(role, recipient);
        }
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

    public PrismStateAction withStateActionAssignments(PrismRoleGroup roles, PrismRole recipient) {
        for (PrismRole role : roles.getRoles()) {
            withStateActionAssignment(role, recipient);
        }
        return this;
    }

    public PrismStateAction withStateActionAssignments(PrismRoleGroup roles, PrismRoleGroup recipients) {
        for (PrismRole role : roles.getRoles()) {
            withStateActionAssignment(role, recipients);
        }
        return this;
    }

    public PrismStateAction withPartnerStateActionAssignment(PrismRole role, PrismRole recipient) {
        PrismStateActionAssignment newAssignment = new PrismStateActionAssignment().withRole(role).withExternalMode();
        return addRecipientAssignment(newAssignment, recipient, "recipient");
    }

    public PrismStateAction withPartnerStateActionAssignment(PrismRole role, PrismRoleGroup recipients) {
        for (PrismRole recipient : recipients.getRoles()) {
            withPartnerStateActionAssignment(role, recipient);
        }
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

    public PrismStateAction withPartnerStateActionAssignments(PrismRoleGroup roles, PrismRole recipient) {
        for (PrismRole role : roles.getRoles()) {
            withPartnerStateActionAssignment(role, recipient);
        }
        return this;
    }

    public PrismStateAction withPartnerStateActionAssignments(PrismRoleGroup roles, PrismRoleGroup recipients) {
        for (PrismRole role : roles.getRoles()) {
            withPartnerStateActionAssignment(role, recipients);
        }
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

    public PrismStateAction withPartnerStateActionRecipientAssignments(PrismRoleGroup roles, PrismRoleGroup recipients) {
        for (PrismRole role : roles.getRoles()) {
            PrismStateActionAssignment newAssignment = new PrismStateActionAssignment().withRole(role);
            for (PrismRole recipient : recipients.getRoles()) {
                addRecipientAssignment(newAssignment, recipient, "partnerRecipient");
            }
        }
        return this;
    }

    public PrismStateAction withStateTransitions(PrismStateTransition... stateTransitions) {
        this.stateTransitions.addAll(Arrays.asList(stateTransitions));
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

    private PrismStateAction addRecipientAssignment(PrismStateActionAssignment newAssignment, PrismRole recipient, String recipientProperty) {
        for (PrismStateActionAssignment assignment : this.stateActionAssignments) {
            if (assignment.equals(newAssignment)) {
                addRecipient(assignment, recipient, recipientProperty);
                return this;
            }
        }
        this.stateActionAssignments.add(addRecipient(newAssignment, recipient, recipientProperty));
        return this;
    }

    private PrismStateActionAssignment addRecipient(PrismStateActionAssignment assignment, PrismRole recipient, String recipientProperty) {
        return (PrismStateActionAssignment) invokeMethod(assignment, "add" + capitalize(recipientProperty), recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(action);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        PrismStateAction other = (PrismStateAction) object;
        return equal(action, other.getAction()) && equal(raisesUrgentFlag, other.getRaisesUrgentFlag())
                && equal(replicableSequenceStart, other.getReplicableSequenceStart()) && equal(actionCondition, other.getActionCondition())
                && equal(actionEnhancement, other.getActionEnhancement()) && equal(notificationDefinition, other.getNotificationDefinition())
                && equal(stateActionAssignments, other.getStateActionAssignments()) && equal(stateTransitions, other.getStateTransitions());
    }

}
