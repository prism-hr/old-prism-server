package uk.co.alumeni.prism.workflow.notification.property;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationOpportunityCategoryBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getPropertyLoader()
                .loadLazy(PrismOpportunityCategory.valueOf(propertyLoader.getNotificationDefinitionDTO().getResource().getApplication().getOpportunityCategories())
                        .getDisplayProperty());
    }

}
