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
import com.zuehlke.pgadmissions.rest.representation.configuration.AbstractConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.NotificationConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.StateDurationConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;

public enum PrismConfiguration {

    ACTION_CUSTOM_QUESTION(ActionCustomQuestionConfiguration.class, ActionCustomQuestionDefinition.class, null, null, null), //
    DISPLAY_PROPERTY(DisplayPropertyConfiguration.class, DisplayPropertyDefinition.class, null, null, null), //
    NOTIFICATION(NotificationConfiguration.class, NotificationDefinition.class, NotificationConfigurationRepresentation.class, 1, 28), //
    STATE_DURATION(StateDurationConfiguration.class, StateDurationDefinition.class, StateDurationConfigurationRepresentation.class, 1, 168), //
    WORKFLOW_PROPERTY(WorkflowPropertyConfiguration.class, WorkflowPropertyDefinition.class, WorkflowPropertyConfigurationRepresentation.class, null, null);

    private Class<? extends WorkflowConfiguration> configurationClass;

    private Class<? extends WorkflowDefinition> definitionClass;

    private Class<? extends AbstractConfigurationRepresentation> representationClass;

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    private static final Map<Class<? extends WorkflowConfiguration>, PrismConfiguration> reverseMap = Maps.newHashMap();

    static {
        for (PrismConfiguration type : values()) {
            reverseMap.put(type.getConfigurationClass(), type);
        }
    }

    private PrismConfiguration(Class<? extends WorkflowConfiguration> configurationClass, Class<? extends WorkflowDefinition> definitionClass,
            Class<? extends AbstractConfigurationRepresentation> representationClass, Integer minimumPermitted, Integer maximumPermitted) {
        this.configurationClass = configurationClass;
        this.definitionClass = definitionClass;
        this.representationClass = representationClass;
        this.minimumPermitted = minimumPermitted;
        this.maximumPermitted = maximumPermitted;
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

    public final Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public final Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public String getDefinitionPropertyName() {
        return UPPER_CAMEL.to(LOWER_CAMEL, definitionClass.getSimpleName());
    }
}
