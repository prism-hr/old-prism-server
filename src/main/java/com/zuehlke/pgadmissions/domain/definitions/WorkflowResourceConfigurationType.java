package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.*;
import com.zuehlke.pgadmissions.rest.representation.configuration.AbstractConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.NotificationConfigurationRepresentation;

import java.util.Map;

public enum WorkflowResourceConfigurationType {

    CUSTOM_QUESTION(ActionCustomQuestion.class, null, "action"), //
    DISPLAY_PROPERTY(DisplayPropertyConfiguration.class, null, "displayPropertyDefinition"), //
    NOTIFICATION(NotificationConfiguration.class, NotificationConfigurationRepresentation.class, "notificationDefinition"), //
    STATE_DURATION(StateDurationConfiguration.class, null, "stateDurationDefinition"), //
    WORKFLOW_PROPERTY(WorkflowPropertyConfiguration.class, null, "workflowPropertyDefinition");

    private Class<? extends WorkflowConfiguration> configurationClass;

    private Class<? extends AbstractConfigurationRepresentation> representationClass;

    private String definitionPropertyName;

    private static final Map<Class<? extends WorkflowConfiguration>, WorkflowResourceConfigurationType> reverseMap = Maps.newHashMap();

    static {
        for (WorkflowResourceConfigurationType type : values()) {
            reverseMap.put(type.getConfigurationClass(), type);
        }
    }

    private WorkflowResourceConfigurationType(Class<? extends WorkflowConfiguration> configurationClass,
            Class<? extends AbstractConfigurationRepresentation> representationClass, String definitionPropertyName) {
        this.configurationClass = configurationClass;
        this.representationClass = representationClass;
        this.definitionPropertyName = definitionPropertyName;
    }

    public Class<? extends WorkflowConfiguration> getConfigurationClass() {
        return configurationClass;
    }

    public Class<? extends AbstractConfigurationRepresentation> getRepresentationClass() {
        return representationClass;
    }

    public String getDefinitionPropertyName() {
        return definitionPropertyName;
    }
}
