package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationOpportunityTypeBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        return propertyLoader.getPropertyLoader().load(PrismDisplayPropertyDefinition.valueOf("SYSTEM_OPPORTUNITY_TYPE_"
                + propertyLoader.getNotificationDefinitionModelDTO().getResource().getApplication().getOpportunityType().name()));
    }

}
