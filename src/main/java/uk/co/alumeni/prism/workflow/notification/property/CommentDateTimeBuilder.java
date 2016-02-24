package uk.co.alumeni.prism.workflow.notification.property;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_TIME_FORMAT;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class CommentDateTimeBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getNotificationDefinitionDTO().getComment()
                .getCreatedTimestampDisplay(propertyLoader.getPropertyLoader().loadLazy(SYSTEM_DATE_TIME_FORMAT));
    }

}
