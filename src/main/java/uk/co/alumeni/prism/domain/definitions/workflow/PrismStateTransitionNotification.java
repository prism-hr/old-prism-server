package uk.co.alumeni.prism.domain.definitions.workflow;

import com.google.common.base.Objects;

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
        return Objects.hashCode(role, notificationDefinition);
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
        return Objects.equal(role, other.getRole()) && Objects.equal(notificationDefinition, other.getNotificationDefinition());
    }

}
