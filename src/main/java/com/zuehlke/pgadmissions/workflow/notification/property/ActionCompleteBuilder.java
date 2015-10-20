package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DECLINE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROCEED;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ActionCompleteBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getRedirectionControl(SYSTEM_PROCEED, propertyLoader.getNotificationDefinitionDTO().getTransitionAction()
                .isDeclinableAction() ? SYSTEM_DECLINE : null);
    }

}
