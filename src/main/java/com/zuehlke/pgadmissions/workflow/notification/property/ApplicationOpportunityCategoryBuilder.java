package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationOpportunityCategoryBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        return propertyLoader.getPropertyLoader()
                .loadLazy(PrismOpportunityCategory.valueOf(propertyLoader.getNotificationDefinitionModelDTO().getResource().getApplication().getOpportunityCategories())
                        .getDisplayProperty());
    }

}
