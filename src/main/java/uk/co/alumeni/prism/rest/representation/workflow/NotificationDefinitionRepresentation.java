package uk.co.alumeni.prism.rest.representation.workflow;

import java.util.List;

import com.google.common.collect.Lists;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionProperty;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory;

public class NotificationDefinitionRepresentation extends StateDurationDefinitionRepresentation {

    private Enum<?> reminderId;

    private List<NotificationDefinitionPropertyCategoryRepresentation> propertyCategories = Lists.newArrayList();

    public final Enum<?> getReminderId() {
        return reminderId;
    }

    public final void setReminderId(Enum<?> reminderId) {
        this.reminderId = reminderId;
    }

    public final List<NotificationDefinitionPropertyCategoryRepresentation> getPropertyCategories() {
        return propertyCategories;
    }

    public final void setPropertyCategories(List<NotificationDefinitionPropertyCategoryRepresentation> propertyCategories) {
        this.propertyCategories = propertyCategories;
    }

    public NotificationDefinitionRepresentation withId(Enum<?> id) {
        setId(id);
        return this;
    }

    public NotificationDefinitionRepresentation withReminderId(Enum<?> reminderId) {
        this.reminderId = reminderId;
        return this;
    }

    public NotificationDefinitionRepresentation withMinimumPermitted(Integer minimumPermitted) {
        setMinimumPermitted(minimumPermitted);
        return this;
    }

    public NotificationDefinitionRepresentation withMaximumPermitted(Integer maximumPermitted) {
        setMaximumPermitted(maximumPermitted);
        return this;
    }

    public NotificationDefinitionRepresentation addPropertyCategory(PrismNotificationDefinitionPropertyCategory propertyCategory) {
        propertyCategories.add(new NotificationDefinitionPropertyCategoryRepresentation().withId(propertyCategory).withProperties(
                propertyCategory.getProperties()));
        return this;
    }

    public static class NotificationDefinitionPropertyCategoryRepresentation {

        private PrismNotificationDefinitionPropertyCategory id;

        private List<PrismNotificationDefinitionProperty> properties;

        public final PrismNotificationDefinitionPropertyCategory getId() {
            return id;
        }

        public final void setId(PrismNotificationDefinitionPropertyCategory id) {
            this.id = id;
        }

        public final List<PrismNotificationDefinitionProperty> getProperties() {
            return properties;
        }

        public final void setProperties(List<PrismNotificationDefinitionProperty> properties) {
            this.properties = properties;
        }

        public NotificationDefinitionPropertyCategoryRepresentation withId(PrismNotificationDefinitionPropertyCategory id) {
            this.id = id;
            return this;
        }

        public NotificationDefinitionPropertyCategoryRepresentation withProperties(List<PrismNotificationDefinitionProperty> properties) {
            this.properties = properties;
            return this;
        }

    }

}
