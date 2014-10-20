package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.base.Objects;

public class PrismStateActionNotification {
    
    private PrismRole role;
    
    private PrismNotificationTemplate template;

    public PrismRole getRole() {
        return role;
    }

    public PrismNotificationTemplate getTemplate() {
        return template;
    }

    public PrismStateActionNotification withRole(PrismRole role) {
        this.role = role;
        return this;
    }
    
    public PrismStateActionNotification withTemplate(PrismNotificationTemplate template) {
        this.template = template;
        return this;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(role, template);
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
        return Objects.equal(role, other.getRole()) && Objects.equal(template, other.getTemplate());
    }
    
}
