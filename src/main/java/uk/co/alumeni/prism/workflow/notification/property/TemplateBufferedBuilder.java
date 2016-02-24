package uk.co.alumeni.prism.workflow.notification.property;

import static org.jboss.util.Strings.EMPTY;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_BUFFERED;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class TemplateBufferedBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return BooleanUtils.isTrue(propertyLoader.getNotificationDefinitionDTO().getBuffered()) ? propertyLoader.getPropertyLoader().loadLazy(SYSTEM_NOTIFICATION_BUFFERED) : EMPTY;
    }

}
