package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ProjectNameBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getNotificationDefinitionDTO().getResource().getProject().getName();
    }

}
