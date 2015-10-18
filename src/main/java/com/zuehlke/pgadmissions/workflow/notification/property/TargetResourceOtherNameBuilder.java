package com.zuehlke.pgadmissions.workflow.notification.property;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

public class TargetResourceOtherNameBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        return propertyLoader.getNotificationDefinitionDTO().getAdvertTarget().getOtherAdvert().getResource().getDisplayName();
    }

}
