package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationOpportunityTypeBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        Resource resource = propertyLoader.getNotificationDefinitionModelDTO().getResource();
        if (ResourceOpportunity.class.isAssignableFrom(resource.getParentResource().getClass())) {
            return propertyLoader.getPropertyLoader().loadLazy(PrismOpportunityType
                    .valueOf(propertyLoader.getNotificationDefinitionModelDTO().getResource().getApplication().getOpportunityType().name()).getDisplayProperty());
        }
        return null;
    }

}
