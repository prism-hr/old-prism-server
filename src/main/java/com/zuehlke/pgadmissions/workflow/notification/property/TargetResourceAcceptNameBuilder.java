package com.zuehlke.pgadmissions.workflow.notification.property;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

public class TargetResourceAcceptNameBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        return propertyLoader.getNotificationDefinitionDTO().getAdvertTarget().getAcceptAdvert().getResource().getDisplayName();
    }

}
