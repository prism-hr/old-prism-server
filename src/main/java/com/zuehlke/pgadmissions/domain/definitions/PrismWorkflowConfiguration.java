package com.zuehlke.pgadmissions.domain.definitions;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import java.util.Map;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestion;
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

public enum PrismWorkflowConfiguration {

    CUSTOM_QUESTION(ActionCustomQuestion.class, Action.class, null), //
    DISPLAY_PROPERTY(DisplayPropertyConfiguration.class, DisplayPropertyDefinition.class, null), //
    NOTIFICATION(NotificationConfiguration.class, NotificationDefinition.class, NotificationConfigurationRepresentation.class), //
    STATE_DURATION(StateDurationConfiguration.class, StateDurationDefinition.class, StateDurationConfigurationRepresentation.class), //
    WORKFLOW_PROPERTY(WorkflowPropertyConfiguration.class, WorkflowPropertyDefinition.class, WorkflowPropertyConfigurationRepresentation.class);

    private Class<? extends WorkflowConfiguration> configurationClass;

    private Class<? extends WorkflowDefinition> definitionClass;

    private Class<? extends AbstractConfigurationRepresentation> representationClass;

    private static final Map<Class<? extends WorkflowConfiguration>, PrismWorkflowConfiguration> reverseMap = Maps.newHashMap();

    static {
        for (PrismWorkflowConfiguration type : values()) {
            reverseMap.put(type.getConfigurationClass(), type);
        }
    }

    private PrismWorkflowConfiguration(Class<? extends WorkflowConfiguration> configurationClass, Class<? extends WorkflowDefinition> definitionClass,
            Class<? extends AbstractConfigurationRepresentation> representationClass) {
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
