package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_AVAILABLE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_UNAVAILABLE;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationInterviewAvailableBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getPropertyLoader().loadLazy(SYSTEM_NOTIFICATION_AVAILABLE, SYSTEM_NOTIFICATION_UNAVAILABLE,
                isTrue(propertyLoader.getNotificationDefinitionDTO().getComment().getInterviewAvailable()));
    }

}
