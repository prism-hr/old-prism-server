package uk.co.alumeni.prism.workflow.notification.property;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DECLINE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROCEED;

@Component
public class ActionCompleteBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getRedirectionControl(SYSTEM_PROCEED, propertyLoader.getNotificationDefinitionDTO().getTransitionAction()
                .isDeclinableAction() ? SYSTEM_DECLINE : null);
    }

}
