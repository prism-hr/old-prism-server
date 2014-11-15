package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.workflow.*;
import com.zuehlke.pgadmissions.rest.representation.configuration.AbstractConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.NotificationConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.StateDurationConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;

import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public enum WorkflowResourceConfigurationType {

    CUSTOM_QUESTION(ActionCustomQuestion.class, Action.class, null),
    DISPLAY_PROPERTY(DisplayPropertyConfiguration.class, DisplayPropertyDefinition.class, null),
    NOTIFICATION(NotificationConfiguration.class, NotificationDefinition.class, NotificationConfigurationRepresentation.class),
    STATE_DURATION(StateDurationConfiguration.class, StateDurationDefinition.class, StateDurationConfigurationRepresentation.class),
    WORKFLOW_PROPERTY(WorkflowPropertyConfiguration.class, WorkflowPropertyDefinition.class, WorkflowPropertyConfigurationRepresentation.class);

    private Class<? extends WorkflowConfiguration> configurationClass;

    private Class<? extends WorkflowDefinition> definitionClass;

    private Class<? extends AbstractConfigurationRepresentation> representationClass;

    private static final Map<Class<? extends WorkflowConfiguration>, WorkflowResourceConfigurationType> reverseMap = Maps.newHashMap();

    static {
        for (WorkflowResourceConfigurationType type : values()) {
            reverseMap.put(type.getConfigurationClass(), type);
        }
    }

    WorkflowResourceConfigurationType(Class<? extends WorkflowConfiguration> configurationClass, Class<? extends WorkflowDefinition> definitionClass, Class<? extends AbstractConfigurationRepresentation> representationClass) {
        this.configurationClass = configurationClass;
        this.definitionClass = definitionClass;
        this.representationClass = representationClass;
    }

    public Class<? extends WorkflowConfiguration> getConfigurationClass() {
        return configurationClass;
    }

    public Class<? extends WorkflowDefinition> getDefinitionClass() {
        return definitionClass;
    }

    public Class<? extends AbstractConfigurationRepresentation> getRepresentationClass() {
        return representationClass;
    }

    public String getDefinitionPropertyName() {
        return UPPER_CAMEL.to(LOWER_CAMEL, definitionClass.getSimpleName());
    }
}
