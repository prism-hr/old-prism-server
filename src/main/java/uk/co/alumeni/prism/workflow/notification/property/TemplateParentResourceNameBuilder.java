package uk.co.alumeni.prism.workflow.notification.property;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO_POSITION_SPECIFIED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.dto.NotificationDefinitionDTO;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class TemplateParentResourceNameBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        NotificationDefinitionDTO notificationDefinitionDTO = propertyLoader.getNotificationDefinitionDTO();
        if (notificationDefinitionDTO.getResource().getParentResource().getResourceScope().getScopeCategory().equals(OPPORTUNITY)) {
            return propertyLoader.getNotificationDefinitionDTO().getResource().getParentResource().getDisplayName();
        }
        return propertyLoader.getPropertyLoader().loadLazy(SYSTEM_NO_POSITION_SPECIFIED);
    }

}
