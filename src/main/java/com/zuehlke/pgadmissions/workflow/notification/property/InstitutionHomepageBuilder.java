package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class InstitutionHomepageBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        return propertyLoader.getNotificationDefinitionModelDTO().getResource().getInstitution().getAdvert().getHomepage();
    }

}