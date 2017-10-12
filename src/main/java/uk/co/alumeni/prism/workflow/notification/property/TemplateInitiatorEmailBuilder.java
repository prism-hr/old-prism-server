package uk.co.alumeni.prism.workflow.notification.property;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class TemplateInitiatorEmailBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getNotificationDefinitionDTO().getInitiator().getEmail();
    }

}
