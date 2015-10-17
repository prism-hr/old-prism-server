package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_LOWER_AT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_LOWER_IN;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
public class TemplateParentResourceTitleBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        PropertyLoader loader = propertyLoader.getPropertyLoader();
        return propertyLoader.getNotificationDefinitionModelDTO().getResource().getParentResourceNameDisplay(loader.loadLazy(SYSTEM_LOWER_AT), loader.loadLazy(SYSTEM_LOWER_IN));
    }

}
