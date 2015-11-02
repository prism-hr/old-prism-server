package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class TargetResourceAcceptNameBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        AdvertTarget target = propertyLoader.getNotificationDefinitionDTO().getAdvertTarget();
        return target == null ? null : target.getAcceptAdvert().getResource().getDisplayName();
    }

}
