package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class TemplateResourceScopeBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getPropertyLoader().loadLazy(propertyLoader.getNotificationDefinitionDTO().getResource().getResourceScope().getDisplayProperty()).toLowerCase();
    }

}
