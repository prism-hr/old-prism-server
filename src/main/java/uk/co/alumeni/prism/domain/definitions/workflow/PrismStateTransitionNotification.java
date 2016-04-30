package uk.co.alumeni.prism.domain.definitions.workflow;

import static com.google.common.base.Objects.equal;

import com.google.common.base.Objects;

public class PrismStateTransitionNotification {

    private PrismRole role;

    private PrismNotificationDefinition notificationdDefinition;

    public PrismRole getRole() {
        return role;
    }

    public PrismNotificationDefinition getNotificationdDefinition() {
        return notificationdDefinition;
    }

    public PrismStateTransitionNotification withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public PrismStateTransitionNotification withNotificationDefinition(PrismNotificationDefinition notificationDefinition) {
        this.notificationdDefinition = notificationDefinition;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(role);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        PrismStateTransitionNotification other = (PrismStateTransitionNotification) object;
        return equal(role, other.getRole()) && equal(notificationdDefinition, other.getNotificationdDefinition());
    }

}
