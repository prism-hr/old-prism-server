package uk.co.alumeni.prism.workflow.notification.property;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class TargetResourceAcceptNameBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        AdvertTarget target = propertyLoader.getNotificationDefinitionDTO().getAdvertTarget();
        return target == null ? null : target.getAcceptAdvert().getResource().getDisplayName();
    }

}
