package uk.co.alumeni.prism.workflow.notification.property;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_INSTITUTIONS;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class SystemInstitutionHomepageBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getRedirectionControl(SYSTEM_INSTITUTIONS);
    }

}
