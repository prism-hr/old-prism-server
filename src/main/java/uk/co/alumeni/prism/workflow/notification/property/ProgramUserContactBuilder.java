package uk.co.alumeni.prism.workflow.notification.property;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class ProgramUserContactBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getNotificationDefinitionDTO().getResource().getProgram().getUser().toString();
    }

}
