package uk.co.alumeni.prism.workflow.notification.property;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_FORMAT;

@Component
public class ApplicationStartDateBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getNotificationDefinitionDTO().getResource().getApplication()
                .getOfferedStartDateDisplay(propertyLoader.getPropertyLoader().loadLazy(SYSTEM_DATE_FORMAT));
    }

}
