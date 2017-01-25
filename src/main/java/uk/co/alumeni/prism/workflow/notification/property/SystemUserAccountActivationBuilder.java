package uk.co.alumeni.prism.workflow.notification.property;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ACTIVATE_ACCOUNT;

@Component
public class SystemUserAccountActivationBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getRedirectionControl(SYSTEM_ACTIVATE_ACCOUNT);
    }

}
