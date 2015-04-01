package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.workflow.*;
import com.zuehlke.pgadmissions.rest.representation.configuration.*;
import com.zuehlke.pgadmissions.rest.representation.workflow.NotificationDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.StateDurationDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowPropertyDefinitionRepresentation;

import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public enum PrismConfiguration {

    CUSTOM_QUESTION(ActionCustomQuestionConfiguration.class, ActionCustomQuestionDefinition.class, ActionCustomQuestionConfigurationRepresentation.class,
            WorkflowDefinitionRepresentation.class, true, true, null, null, "_COMMENT_UPDATED_ACTION_PROPERTY", false, false, new String[] { "id" }), //
    DISPLAY_PROPERTY(DisplayPropertyConfiguration.class, DisplayPropertyDefinition.class, DisplayPropertyConfigurationRepresentation.class,
            WorkflowDefinitionRepresentation.class, true, false, null, null, "_COMMENT_UPDATED_DISPLAY_PROPERTY", true, true, new String[] { "category", "id" }), //
    NOTIFICATION(NotificationConfiguration.class, NotificationDefinition.class, NotificationConfigurationRepresentation.class,
            NotificationDefinitionRepresentation.class, false, false, 1, 28, "_COMMENT_UPDATED_NOTIFICATION", true, false, new String[] { "id" }), //
    STATE_DURATION(StateDurationConfiguration.class, StateDurationDefinition.class, StateDurationConfigurationRepresentation.class,
            StateDurationDefinitionRepresentation.class, true, false, 1, 168, "_COMMENT_UPDATED_STATE_DURATION", true, true, new String[] { "id" }), //
    WORKFLOW_PROPERTY(WorkflowPropertyConfiguration.class, WorkflowPropertyDefinition.class, WorkflowPropertyConfigurationRepresentation.class,
            WorkflowPropertyDefinitionRepresentation.class, true, true, null, null, "_COMMENT_UPDATED_WORKFLOW_PROPERTY", true, true, new String[] {
                    "category", "id" });

    private Class<? extends WorkflowConfiguration> configurationClass;

    private Class<? extends WorkflowDefinition> definitionClass;

    private Class<? extends WorkflowConfigurationRepresentation> configurationRepresentationClass;

    private Class<? extends WorkflowDefinitionRepresentation> definitionRepresentationClass;

    private boolean grouped;

    private boolean versioned;

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    private String updateCommentProperty;

    private boolean validateResponseSize;

    private boolean localizable;

    private String[] orderColumns;

    private static final Map<Class<? extends WorkflowConfiguration>, PrismConfiguration> reverseMap = Maps.newHashMap();

    static {
        for (PrismConfiguration type : values()) {
            reverseMap.put(type.getConfigurationClass(), type);
        }
    }

    private PrismConfiguration(Class<? extends WorkflowConfiguration> configurationClass, Class<? extends WorkflowDefinition> definitionClass,
            Class<? extends WorkflowConfigurationRepresentation> configurationRepresentationClass,
            Class<? extends WorkflowDefinitionRepresentation> definitionRepresentationClass, boolean grouped, boolean versioned, Integer minimumPermitted,
            Integer maximumPermitted, String updateCommentProperty, boolean validateResponseSize, boolean localizable, String[] orderColumns) {
        this.configurationClass = configurationClass;
        this.definitionClass = definitionClass;
        this.configurationRepresentationClass = configurationRepresentationClass;
        this.definitionRepresentationClass = definitionRepresentationClass;
        this.grouped = grouped;
        this.versioned = versioned;
        this.minimumPermitted = minimumPermitted;
        this.maximumPermitted = maximumPermitted;
        this.updateCommentProperty = updateCommentProperty;
        this.validateResponseSize = validateResponseSize;
        this.localizable = localizable;
        this.orderColumns = orderColumns;
    }

    public Class<? extends WorkflowConfiguration> getConfigurationClass() {
        return configurationClass;
    }

    public Class<? extends WorkflowDefinition> getDefinitionClass() {
        return definitionClass;
    }

    public Class<? extends WorkflowConfigurationRepresentation> getConfigurationRepresentationClass() {
        return configurationRepresentationClass;
    }

    public final Class<? extends WorkflowDefinitionRepresentation> getDefinitionRepresentationClass() {
        return definitionRepresentationClass;
    }

    public final boolean isGrouped() {
        return grouped;
    }

    public final boolean isVersioned() {
        return versioned;
    }

    public final Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public final Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public final String getUpdateCommentProperty() {
        return updateCommentProperty;
    }

    public String getDefinitionPropertyName() {
        return UPPER_CAMEL.to(LOWER_CAMEL, definitionClass.getSimpleName());
    }

    public boolean isValidateResponseSize() {
        return validateResponseSize;
    }

    public final boolean isLocalizable() {
        return localizable;
    }

    public final String[] getOrderColumns() {
        return orderColumns;
    }

}
