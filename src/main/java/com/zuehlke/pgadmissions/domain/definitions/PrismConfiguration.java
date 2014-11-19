package com.zuehlke.pgadmissions.domain.definitions;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import java.util.Map;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.rest.representation.configuration.NotificationConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.StateDurationConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.NotificationDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.StateDurationDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowPropertyDefinitionRepresentation;

public enum PrismConfiguration {

    ACTION_CUSTOM_QUESTION(ActionCustomQuestionConfiguration.class, ActionCustomQuestionDefinition.class, null, WorkflowDefinitionRepresentation.class, true,
            true, null, null, "_COMMENT_UPDATED_ACTION_PROPERTY"), //
    DISPLAY_PROPERTY(DisplayPropertyConfiguration.class, DisplayPropertyDefinition.class, null, WorkflowDefinitionRepresentation.class, true, false, null,
            null, "_COMMENT_UPDATED_DISPLAY_PROPERTY"), //
    NOTIFICATION(NotificationConfiguration.class, NotificationDefinition.class, NotificationConfigurationRepresentation.class,
            NotificationDefinitionRepresentation.class, false, false, 1, 28, "_COMMENT_UPDATED_NOTIFICATION"), //
    STATE_DURATION(StateDurationConfiguration.class, StateDurationDefinition.class, StateDurationConfigurationRepresentation.class,
            StateDurationDefinitionRepresentation.class, true, false, 1, 168, "_COMMENT_UPDATED_STATE_DURATION"), //
    WORKFLOW_PROPERTY(WorkflowPropertyConfiguration.class, WorkflowPropertyDefinition.class, WorkflowPropertyConfigurationRepresentation.class,
            WorkflowPropertyDefinitionRepresentation.class, true, true, null, null, "_COMMENT_UPDATED_WORKFLOW_PROPERTY");

    private Class<? extends WorkflowConfiguration> configurationClass;

    private Class<? extends WorkflowDefinition> definitionClass;

    private Class<? extends WorkflowConfigurationRepresentation> configurationRepresentationClass;

    private Class<? extends WorkflowDefinitionRepresentation> definitionRepresentationClass;

    private boolean grouped;

    private boolean versioned;

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    private String updateCommentProperty;

    private static final Map<Class<? extends WorkflowConfiguration>, PrismConfiguration> reverseMap = Maps.newHashMap();

    static {
        for (PrismConfiguration type : values()) {
            reverseMap.put(type.getConfigurationClass(), type);
        }
    }

    private PrismConfiguration(Class<? extends WorkflowConfiguration> configurationClass, Class<? extends WorkflowDefinition> definitionClass,
            Class<? extends WorkflowConfigurationRepresentation> configurationRepresentationClass,
            Class<? extends WorkflowDefinitionRepresentation> definitionRepresentationClass, boolean grouped, boolean versioned, Integer minimumPermitted,
            Integer maximumPermitted, String updateCommentProperty) {
        this.configurationClass = configurationClass;
        this.grouped = grouped;
        this.versioned = versioned;
        this.definitionClass = definitionClass;
        this.configurationRepresentationClass = configurationRepresentationClass;
        this.definitionRepresentationClass = definitionRepresentationClass;
        this.minimumPermitted = minimumPermitted;
        this.maximumPermitted = maximumPermitted;
        this.updateCommentProperty = updateCommentProperty;
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
}
