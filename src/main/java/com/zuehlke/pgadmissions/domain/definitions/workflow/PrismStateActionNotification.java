package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.base.Objects;

public class PrismStateActionNotification {
    
    private PrismRole role;
    
    private PrismNotificationDefinition notification;

    public PrismRole getRole() {
        return role;
    }

    public PrismNotificationDefinition getNotification() {
        return notification;
    }

    public PrismStateActionNotification withRole(PrismRole role) {
        this.role = role;
        return this;
    }
    
    public PrismStateActionNotification withDefinition(PrismNotificationDefinition notification) {
        this.notification = notification;
        return this;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(role, notification);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PrismStateActionNotification other = (PrismStateActionNotification) obj;
        return Objects.equal(role, other.getRole()) && Objects.equal(notification, other.getNotification());
    }
    
}
