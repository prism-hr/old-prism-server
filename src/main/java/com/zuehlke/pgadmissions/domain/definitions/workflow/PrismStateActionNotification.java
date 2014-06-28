package com.zuehlke.pgadmissions.domain.definitions.workflow;


public class PrismStateActionNotification {
    
    PrismRole role;
    
    PrismNotificationTemplate template;

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
    
}
