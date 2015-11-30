package uk.co.alumeni.prism.domain.definitions;

import java.util.Map;

import com.google.common.collect.Maps;

import uk.co.alumeni.prism.domain.display.DisplayPropertyConfiguration;
import uk.co.alumeni.prism.domain.display.DisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.workflow.NotificationConfiguration;
import uk.co.alumeni.prism.domain.workflow.NotificationDefinition;
import uk.co.alumeni.prism.domain.workflow.StateDurationConfiguration;
import uk.co.alumeni.prism.domain.workflow.StateDurationDefinition;
import uk.co.alumeni.prism.domain.workflow.WorkflowConfiguration;
import uk.co.alumeni.prism.domain.workflow.WorkflowDefinition;
import uk.co.alumeni.prism.rest.representation.configuration.DisplayPropertyConfigurationRepresentation;
import uk.co.alumeni.prism.rest.representation.configuration.NotificationConfigurationRepresentation;
import uk.co.alumeni.prism.rest.representation.configuration.StateDurationConfigurationRepresentation;
import uk.co.alumeni.prism.rest.representation.configuration.WorkflowConfigurationRepresentation;

public enum PrismConfiguration {

    DISPLAY_PROPERTY(DisplayPropertyConfiguration.class, DisplayPropertyDefinition.class, DisplayPropertyConfigurationRepresentation.class,
            true, false, null, null, "_COMMENT_UPDATED_DISPLAY_PROPERTY", true, new String[] { "category", "id" }), //
    NOTIFICATION(NotificationConfiguration.class, NotificationDefinition.class, NotificationConfigurationRepresentation.class,
            false, false, null, null, "_COMMENT_UPDATED_NOTIFICATION", true, new String[] { "id" }), //
    STATE_DURATION(StateDurationConfiguration.class, StateDurationDefinition.class, StateDurationConfigurationRepresentation.class,
            true, false, 1, 364, "_COMMENT_UPDATED_STATE_DURATION", true, new String[] { "id" }); //

    private Class<? extends WorkflowConfiguration<?>> configurationClass;

    private Class<? extends WorkflowDefinition> definitionClass;

    private Class<? extends WorkflowConfigurationRepresentation> configurationRepresentationClass;

    private boolean grouped;

    private boolean versioned;

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    private String updateCommentProperty;

    private boolean validateResponseSize;

    private String[] orderColumns;

    private static Map<Class<? extends WorkflowConfiguration<?>>, PrismConfiguration> reverseMap = Maps.newHashMap();

    static {
        for (PrismConfiguration type : values()) {
            reverseMap.put(type.getConfigurationClass(), type);
        }
    }

    private PrismConfiguration(Class<? extends WorkflowConfiguration<?>> configurationClass, Class<? extends WorkflowDefinition> definitionClass,
            Class<? extends WorkflowConfigurationRepresentation> configurationRepresentationClass, boolean grouped, boolean versioned,
            Integer minimumPermitted, Integer maximumPermitted, String updateCommentProperty, boolean validateResponseSize, String[] orderColumns) {
        this.configurationClass = configurationClass;
        this.definitionClass = definitionClass;
        this.configurationRepresentationClass = configurationRepresentationClass;
        this.grouped = grouped;
        this.versioned = versioned;
        this.minimumPermitted = minimumPermitted;
        this.maximumPermitted = maximumPermitted;
        this.updateCommentProperty = updateCommentProperty;
        this.validateResponseSize = validateResponseSize;
        this.orderColumns = orderColumns;
    }

    public Class<? extends WorkflowConfiguration<?>> getConfigurationClass() {
        return configurationClass;
    }

    public Class<? extends WorkflowDefinition> getDefinitionClass() {
        return definitionClass;
    }

    public Class<? extends WorkflowConfigurationRepresentation> getConfigurationRepresentationClass() {
        return configurationRepresentationClass;
    }

    public boolean isGrouped() {
        return grouped;
    }

    public boolean isVersioned() {
        return versioned;
    }

    public Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public String getUpdateCommentProperty() {
        return updateCommentProperty;
    }

    public boolean isValidateResponseSize() {
        return validateResponseSize;
    }

    public boolean isCategorizable() {
        try {
            return PrismConfigurationCategorizable.class.isAssignableFrom(definitionClass.getDeclaredField("id").getType());
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    public String[] getOrderColumns() {
        return orderColumns;
    }

}
