package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.rest.representation.workflow.NotificationDefinitionRepresentation;

public class NotificationDefinitionConverter extends DozerConverter<NotificationDefinition, NotificationDefinitionRepresentation> {

    public NotificationDefinitionConverter() {
        super(NotificationDefinition.class, NotificationDefinitionRepresentation.class);
    }

    @Override
    public NotificationDefinitionRepresentation convertTo(NotificationDefinition source, NotificationDefinitionRepresentation destination) {
        if (source != null) {
            PrismConfiguration prismConfiguration = PrismConfiguration.NOTIFICATION;
            PrismNotificationDefinition prismDefinition = source.getId();
            PrismNotificationDefinition prismReminderDefinition = prismDefinition.getReminderDefinition();
            destination = new NotificationDefinitionRepresentation().withId(prismDefinition).withReminderId(prismReminderDefinition)
                    .withMinimumPermitted(prismReminderDefinition == null ? null : prismConfiguration.getMinimumPermitted())
                    .withMaximumPermitted(prismReminderDefinition == null ? null : prismConfiguration.getMaximumPermitted());
            for (PrismNotificationDefinitionPropertyCategory propertyCategory : prismDefinition.getPropertyCategories()) {
                destination.addPropertyCategory(propertyCategory);
            }
            return destination;
        }
        return null;
    }

    @Override
    public NotificationDefinition convertFrom(NotificationDefinitionRepresentation source, NotificationDefinition destination) {
        throw new UnsupportedOperationException();
    }

}
