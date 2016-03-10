package uk.co.alumeni.prism.workflow.notification.property;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DEPARTMENTS;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class SystemDepartmentHomepageBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getRedirectionControl(SYSTEM_DEPARTMENTS);
    }

}
