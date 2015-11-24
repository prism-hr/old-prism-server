package uk.co.alumeni.prism.workflow.notification.property;


import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_AVAILABLE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_UNAVAILABLE;

@Component
public class ApplicationInterviewAvailableBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getPropertyLoader().loadLazy(SYSTEM_NOTIFICATION_AVAILABLE, SYSTEM_NOTIFICATION_UNAVAILABLE,
                BooleanUtils.isTrue(propertyLoader.getNotificationDefinitionDTO().getComment().getInterviewAvailable()));
    }

}
