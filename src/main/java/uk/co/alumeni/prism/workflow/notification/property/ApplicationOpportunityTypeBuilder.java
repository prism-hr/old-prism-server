package uk.co.alumeni.prism.workflow.notification.property;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationOpportunityTypeBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        Resource resource = propertyLoader.getNotificationDefinitionDTO().getResource();
        if (ResourceOpportunity.class.isAssignableFrom(resource.getParentResource().getClass())) {
            return propertyLoader.getPropertyLoader().loadLazy(PrismOpportunityType
                    .valueOf(propertyLoader.getNotificationDefinitionDTO().getResource().getApplication().getOpportunityType().name()).getDisplayProperty());
        }
        return null;
    }

}
