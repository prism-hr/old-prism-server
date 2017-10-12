package uk.co.alumeni.prism.domain.definitions.workflow;

import com.google.common.base.Objects;

import static com.google.common.base.Objects.equal;

public class PrismStateTransitionNotification {

    private PrismRole role;

    private PrismNotificationDefinition notificationDefinition;

    public PrismRole getRole() {
        return role;
    }

    public PrismNotificationDefinition getNotificationDefinition() {
        return notificationDefinition;
    }

    public PrismStateTransitionNotification withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public PrismStateTransitionNotification withNotificationDefinition(PrismNotificationDefinition notificationDefinition) {
        this.notificationDefinition = notificationDefinition;
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
        return equal(role, other.getRole()) && equal(notificationDefinition, other.getNotificationDefinition());
    }

}
