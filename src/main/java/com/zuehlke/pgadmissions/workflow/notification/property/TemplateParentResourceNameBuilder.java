package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO_POSITION_SPECIFIED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dto.NotificationDefinitionDTO;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class TemplateParentResourceNameBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        NotificationDefinitionDTO notificationDefinitionDTO = propertyLoader.getNotificationDefinitionDTO();
        if (notificationDefinitionDTO.getResource().getResourceScope().getScopeCategory().equals(OPPORTUNITY)) {
            return propertyLoader.getNotificationDefinitionDTO().getResource().getParentResource().getDisplayName();
        }
        return propertyLoader.getPropertyLoader().loadLazy(SYSTEM_NO_POSITION_SPECIFIED);
    }

}
