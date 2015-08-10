package com.zuehlke.pgadmissions.mapping;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.*;
import com.zuehlke.pgadmissions.rest.dto.ActionCustomQuestionConfigurationDTO.ActionCustomQuestionConfigurationValueDTO;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO.StateDurationConfigurationValueDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.*;
import com.zuehlke.pgadmissions.rest.representation.workflow.NotificationDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.StateDurationDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowPropertyDefinitionRepresentation;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.STATE_DURATION;

@Service
@Transactional
public class CustomizationMapper {

    @SuppressWarnings("unchecked")
    public <T extends WorkflowDefinition, U extends WorkflowDefinitionRepresentation> U getWorkflowDefinitionRepresentation(T definition) {
        Class<T> definitionClass = (Class<T>) definition.getClass();

        if (NotificationDefinition.class.equals(definitionClass)) {
            return (U) getNotificationDefinitionRepresentation((NotificationDefinition) definition);
        } else if (WorkflowPropertyDefinition.class.equals(definitionClass)) {
            return (U) getWorkflowPropertyDefinitionRepresentation((WorkflowPropertyDefinition) definition);
        } else if (StateDurationDefinition.class.equals(definitionClass)) {
            return (U) getStateDurationDefinitionRepresentation((StateDurationDefinition) definition);
        }

        return (U) getWorkflowDefinitionSimpleRepresentation(definition);
    }

    @SuppressWarnings("unchecked")
    public <T extends WorkflowConfigurationDTO, U extends WorkflowConfiguration<?>> U getWorkflowConfiguration(T configurationDTO) {
        Class<T> configurationClass = (Class<T>) configurationDTO.getClass();

        if (ActionCustomQuestionConfigurationValueDTO.class.equals(configurationClass)) {
            return (U) getActionCustomQuestionConfiguration((ActionCustomQuestionConfigurationValueDTO) configurationDTO);
        } else if (DisplayPropertyConfigurationDTO.class.equals(configurationClass)) {
            return (U) getDisplayPropertyConfiguration((DisplayPropertyConfigurationDTO) configurationDTO);
        } else if (NotificationConfigurationDTO.class.equals(configurationClass)) {
            return (U) getNotificationConfiguration((NotificationConfigurationDTO) configurationDTO);
        } else if (StateDurationConfigurationValueDTO.class.equals(configurationClass)) {
            return (U) getStateDurationConfiguration((StateDurationConfigurationValueDTO) configurationDTO);
        }

        return (U) getWorkflowPropertyConfiguration((WorkflowPropertyConfigurationValueDTO) configurationDTO);
    }

    @SuppressWarnings("unchecked")
    public <T extends WorkflowConfiguration<?>> WorkflowConfigurationRepresentation getWorkflowConfigurationRepresentation(T configuration) {
        Class<T> configurationClass = (Class) configuration.getClass();

        if (ActionCustomQuestionConfiguration.class.equals(configurationClass)) {
            return getActionCustomQuestionConfigurationRepresentation((ActionCustomQuestionConfiguration) configuration);
        } else if (DisplayPropertyConfiguration.class.equals(configurationClass)) {
            return getDisplayPropertyConfigurationRepresentation((DisplayPropertyConfiguration) configuration);
        } else if (NotificationConfiguration.class.equals(configurationClass)) {
            return getNotificationConfigurationRepresentation((NotificationConfiguration) configuration);
        } else if (StateDurationConfiguration.class.equals(configurationClass)) {
            return getStateDurationConfigurationRepresentation((StateDurationConfiguration) configuration);
        }

        return getWorkflowPropertyConfigurationRepresentation((WorkflowPropertyConfiguration) configuration);
    }

    public NotificationDefinitionRepresentation getNotificationDefinitionRepresentation(NotificationDefinition definition) {
        PrismConfiguration prismConfiguration = NOTIFICATION;
        PrismNotificationDefinition prismDefinition = definition.getId();
        PrismNotificationDefinition prismReminderDefinition = prismDefinition.getReminderDefinition();

        NotificationDefinitionRepresentation representation = new NotificationDefinitionRepresentation().withId(prismDefinition)
                .withReminderId(prismReminderDefinition)
                .withMinimumPermitted(prismReminderDefinition == null ? null : prismConfiguration.getMinimumPermitted())
                .withMaximumPermitted(prismReminderDefinition == null ? null : prismConfiguration.getMaximumPermitted());

        for (PrismNotificationDefinitionPropertyCategory propertyCategory : prismDefinition.getPropertyCategories()) {
            representation.addPropertyCategory(propertyCategory);
        }

        return representation;
    }

    public WorkflowPropertyDefinitionRepresentation getWorkflowPropertyDefinitionRepresentation(WorkflowPropertyDefinition definition) {
        PrismConfiguration prismConfiguration = STATE_DURATION;
        return new WorkflowPropertyDefinitionRepresentation().withId(definition.getId())
                .withMinimumPermitted(prismConfiguration.getMinimumPermitted())
                .withMaximumPermitted(prismConfiguration.getMaximumPermitted())
                .withDefineRange(definition.getDefineRange())
                .withCanBeDisabled(definition.getCanBeDisabled());
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

    public ActionCustomQuestionConfiguration getActionCustomQuestionConfiguration(ActionCustomQuestionConfigurationValueDTO configurationDTO) {
        String name = configurationDTO.getComponent();
        List<String> options = configurationDTO.getOptions();
        String validation = configurationDTO.getValidation();
        return new ActionCustomQuestionConfiguration().withCustomQuestionType(PrismCustomQuestionType.getByComponentName(name)).withName(name)
                .withEditable(configurationDTO.getEditable()).withIndex(configurationDTO.getIndex()).withLabel(configurationDTO.getLabel())
                .withDescription(configurationDTO.getDescription()).withOptions(options == null ? null : Joiner.on("|").join(options))
                .withRequired(configurationDTO.getRequired()).withValidation(validation).withWeighting(configurationDTO.getWeighting())
                .withPlaceholder(configurationDTO.getPlaceholder());
    }

    public DisplayPropertyConfiguration getDisplayPropertyConfiguration(DisplayPropertyConfigurationDTO configurationDTO) {
        return new DisplayPropertyConfiguration().withValue(configurationDTO.getValue());
    }

    public NotificationConfiguration getNotificationConfiguration(NotificationConfigurationDTO configurationDTO) {
        return new NotificationConfiguration().withSubject(configurationDTO.getSubject()).withContent(configurationDTO.getContent())
                .withReminderInterval(configurationDTO.getReminderInterval());
    }

    public StateDurationConfiguration getStateDurationConfiguration(StateDurationConfigurationValueDTO configurationDTO) {
        return new StateDurationConfiguration().withDuration(configurationDTO.getDuration());
    }

    public WorkflowPropertyConfiguration getWorkflowPropertyConfiguration(WorkflowPropertyConfigurationValueDTO configurationDTO) {
        boolean enabled = configurationDTO.getEnabled();
        PrismWorkflowPropertyDefinition definitionId = configurationDTO.getDefinitionId();

        boolean defineRange = definitionId.isDefineRange();
        int minimum = defineRange && enabled ? configurationDTO.getMinimum() : 0;
        int maximum = defineRange && enabled ? configurationDTO.getMaximum() : 0;

        boolean required = defineRange ? configurationDTO.getMinimum() > 1 : definitionId.isCanBeOptional() ? true : configurationDTO.getRequired();
        return new WorkflowPropertyConfiguration().withEnabled(enabled).withMinimum(minimum).withMaximum(maximum).withRequired(required).withActive(true);
    }

    public ActionCustomQuestionConfigurationRepresentation getActionCustomQuestionConfigurationRepresentation(ActionCustomQuestionConfiguration configuration) {
        return new ActionCustomQuestionConfigurationRepresentation().withProperty(configuration.getDefinition().getId())
                .withId(configuration.getId()).withComponent(configuration.getComponent()).withEditable(configuration.getEditable())
                .withIndex(configuration.getIndex()).withLabel(configuration.getLabel()).withDescription(configuration.getDescription())
                .withPlaceholder(configuration.getPlaceholder()).withOptions(Arrays.asList(configuration.getOptions().split("|")))
                .withRequired(configuration.getRequired()).withValidation(configuration.getValidation()).withWeighting(configuration.getWeighting())
                .withVersion(configuration.getVersion());
    }

    public DisplayPropertyConfigurationRepresentation getDisplayPropertyConfigurationRepresentation(DisplayPropertyConfiguration configuration) {
        return new DisplayPropertyConfigurationRepresentation().withProperty(configuration.getDefinition().getId()).withValue(configuration.getValue());
    }

    public NotificationConfigurationRepresentation getNotificationConfigurationRepresentation(NotificationConfiguration configuration) {
        return new NotificationConfigurationRepresentation().withProperty(configuration.getDefinition().getId()).withSubject(configuration.getSubject())
                .withContent(configuration.getContent()).withReminderInteger(configuration.getReminderInterval());
    }

    public StateDurationConfigurationRepresentation getStateDurationConfigurationRepresentation(StateDurationConfiguration configuration) {
        return new StateDurationConfigurationRepresentation().withProperty(configuration.getDefinition().getId()).withDuration(configuration.getDuration());
    }

    public WorkflowPropertyConfigurationRepresentation getWorkflowPropertyConfigurationRepresentation(WorkflowPropertyConfiguration configuration) {
        WorkflowPropertyDefinition definition = configuration.getDefinition();
        return new WorkflowPropertyConfigurationRepresentation().withProperty(definition.getId()).withCategory(definition.getCategory())
                .withEnabled(configuration.getEnabled()).withRequired(configuration.getRequired()).withMinimum(configuration.getMinimum())
                .withMaximum(configuration.getMaximum()).withVersion(configuration.getVersion());
    }

}
