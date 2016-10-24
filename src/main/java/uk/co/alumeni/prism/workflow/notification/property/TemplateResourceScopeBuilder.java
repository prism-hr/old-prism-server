package uk.co.alumeni.prism.workflow.notification.property;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class TemplateResourceScopeBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getPropertyLoader().loadLazy(propertyLoader.getNotificationDefinitionDTO().getResource().getResourceScope().getDisplayProperty()).toLowerCase();
    }

}
