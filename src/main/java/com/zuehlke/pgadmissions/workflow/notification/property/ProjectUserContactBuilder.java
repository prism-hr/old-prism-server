package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ProjectUserContactBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getNotificationDefinitionDTO().getResource().getProject().getUser().toString();
    }

}
