package uk.co.alumeni.prism.domain.definitions.workflow;

import com.google.common.base.Objects;

public class PrismStateTransitionNotification {

    private PrismRole role;

    private PrismNotificationDefinition notification;

    public PrismRole getRole() {
        return role;
    }

    public PrismNotificationDefinition getNotification() {
        return notification;
    }

    public PrismStateTransitionNotification withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public PrismStateTransitionNotification withDefinition(PrismNotificationDefinition notification) {
        this.notification = notification;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(role, notification);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final PrismStateTransitionNotification other = (PrismStateTransitionNotification) object;
        return Objects.equal(role, other.getRole()) && Objects.equal(notification, other.getNotification());
    }

}
