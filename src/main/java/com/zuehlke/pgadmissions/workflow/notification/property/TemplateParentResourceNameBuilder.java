package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class TemplateParentResourceNameBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        return propertyLoader.getNotificationDefinitionDTO().getResource().getParentResource().getDisplayName();
    }

}
