package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.STATE_DURATION;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO.StateDurationConfigurationValueDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.DisplayPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.NotificationConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.StateDurationConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.NotificationDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.StateDurationDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;

@Service
@Transactional
public class CustomizationMapper {

    @SuppressWarnings("unchecked")
    public <T extends WorkflowDefinition, U extends WorkflowDefinitionRepresentation> U getWorkflowDefinitionRepresentation(T definition) {
        Class<T> definitionClass = (Class<T>) definition.getClass();

        if (NotificationDefinition.class.equals(definitionClass)) {
            return (U) getNotificationDefinitionRepresentation((NotificationDefinition) definition);
        } else if (StateDurationDefinition.class.equals(definitionClass)) {
            return (U) getStateDurationDefinitionRepresentation((StateDurationDefinition) definition);
        }

        return (U) getWorkflowDefinitionSimpleRepresentation(definition);
    }

    @SuppressWarnings("unchecked")
    public <T extends WorkflowConfigurationDTO, U extends WorkflowConfiguration<?>> U getWorkflowConfiguration(T configurationDTO) {
        Class<T> configurationClass = (Class<T>) configurationDTO.getClass();

        if (DisplayPropertyConfigurationDTO.class.equals(configurationClass)) {
            return (U) getDisplayPropertyConfiguration((DisplayPropertyConfigurationDTO) configurationDTO);
        } else if (NotificationConfigurationDTO.class.equals(configurationClass)) {
            return (U) getNotificationConfiguration((NotificationConfigurationDTO) configurationDTO);
        }

        return (U) getStateDurationConfiguration((StateDurationConfigurationValueDTO) configurationDTO);
    }

    @SuppressWarnings("unchecked")
    public <T extends WorkflowConfiguration<?>> WorkflowConfigurationRepresentation getWorkflowConfigurationRepresentation(T configuration) {
        Class<T> configurationClass = (Class<T>) configuration.getClass();

        if (DisplayPropertyConfiguration.class.equals(configurationClass)) {
            return getDisplayPropertyConfigurationRepresentation((DisplayPropertyConfiguration) configuration);
        } else if (NotificationConfiguration.class.equals(configurationClass)) {
            return getNotificationConfigurationRepresentation((NotificationConfiguration) configuration);
        }

        return getStateDurationConfigurationRepresentation((StateDurationConfiguration) configuration);
    }

    public NotificationDefinitionRepresentation getNotificationDefinitionRepresentation(NotificationDefinition definition) {
        PrismNotificationDefinition prismDefinition = definition.getId();
        NotificationDefinitionRepresentation representation = new NotificationDefinitionRepresentation().withId(prismDefinition);

        for (PrismNotificationDefinitionPropertyCategory propertyCategory : prismDefinition.getPropertyCategories()) {
            representation.addPropertyCategory(propertyCategory);
        }

        return representation;
    }

    public StateDurationDefinitionRepresentation getStateDurationDefinitionRepresentation(StateDurationDefinition definition) {
        PrismConfiguration prismConfiguration = STATE_DURATION;
        return new StateDurationDefinitionRepresentation().withId(definition.getId())
                .withMinimumPermitted(prismConfiguration.getMinimumPermitted())
                .withMaximumPermitted(prismConfiguration.getMaximumPermitted());
    }

    public <T extends WorkflowDefinition> WorkflowDefinitionRepresentation getWorkflowDefinitionSimpleRepresentation(T definition) {
        return new WorkflowDefinitionRepresentation().withId(definition.getId());
    }

    public DisplayPropertyConfiguration getDisplayPropertyConfiguration(DisplayPropertyConfigurationDTO configurationDTO) {
        return new DisplayPropertyConfiguration().withValue(configurationDTO.getValue());
    }

    public NotificationConfiguration getNotificationConfiguration(NotificationConfigurationDTO configurationDTO) {
        return new NotificationConfiguration().withSubject(configurationDTO.getSubject()).withContent(configurationDTO.getContent());
    }

    public StateDurationConfiguration getStateDurationConfiguration(StateDurationConfigurationValueDTO configurationDTO) {
        return new StateDurationConfiguration().withDuration(configurationDTO.getDuration());
    }

    public DisplayPropertyConfigurationRepresentation getDisplayPropertyConfigurationRepresentation(DisplayPropertyConfiguration configuration) {
        return new DisplayPropertyConfigurationRepresentation().withProperty(configuration.getDefinition().getId()).withValue(configuration.getValue());
    }

    public NotificationConfigurationRepresentation getNotificationConfigurationRepresentation(NotificationConfiguration configuration) {
        return new NotificationConfigurationRepresentation().withProperty(configuration.getDefinition().getId()).withSubject(configuration.getSubject())
                .withContent(configuration.getContent());
    }

    public StateDurationConfigurationRepresentation getStateDurationConfigurationRepresentation(StateDurationConfiguration configuration) {
        return new StateDurationConfigurationRepresentation().withProperty(configuration.getDefinition().getId()).withDuration(configuration.getDuration());
    }

}
