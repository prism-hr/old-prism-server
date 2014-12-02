package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.base.Objects;

public class PrismStateActionNotification {
    
    private PrismRole role;
    
    private PrismNotificationDefinition definition;

    public PrismRole getRole() {
        return role;
    }

    public PrismNotificationDefinition getDefinition() {
        return definition;
    }

    public PrismStateActionNotification withRole(PrismRole role) {
        this.role = role;
        return this;
    }
    
    public PrismStateActionNotification withDefinition(PrismNotificationDefinition definition) {
        this.definition = definition;
        return this;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(role, definition);
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
        return Objects.equal(role, other.getRole()) && Objects.equal(definition, other.getDefinition());
    }
    
}
