package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.STATE_DURATION;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.rest.dto.ActionCustomQuestionConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationDTO;
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
    public <T extends WorkflowConfigurationDTO, U extends WorkflowConfiguration<?>> U getWorkflowConfigurationRepresentation(T configuratioDTO) {

        Class<T> configurationClass = (Class<T>) configuratioDTO.getClass();
        if (ActionCustomQuestionConfigurationDTO.class.equals(configurationClass)) {
            return (U) getNotificationDefinitionRepresentation((NotificationDefinition) configuratioDTO);
        } else if (DisplayPropertyConfigurationDTO.class.equals(configurationClass)) {
            
        } else if (NotificationConfigurationDTO.class.equals(configurationClass)) {
            return (U) getStateDurationDefinitionRepresentation((StateDurationDefinition) configuratioDTO);
        } else if (StateDurationConfigurationDTO.class.equals(configurationClass)) {
            
        }

        return (U) getWorkflowDefinitionSimpleRepresentation(configuratioDTO);
    }

    public NotificationDefinitionRepresentation getNotificationDefinitionRepresentation(NotificationDefinition notificationDefinition) {
        PrismConfiguration prismConfiguration = NOTIFICATION;
        PrismNotificationDefinition prismDefinition = notificationDefinition.getId();
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

    public StateDurationDefinitionRepresentation getStateDurationDefinitionRepresentation(StateDurationDefinition stateDurationDefinition) {
        PrismConfiguration prismConfiguration = STATE_DURATION;
        return new StateDurationDefinitionRepresentation().withId(stateDurationDefinition.getId())
                .withMinimumPermitted(prismConfiguration.getMinimumPermitted())
                .withMaximumPermitted(prismConfiguration.getMaximumPermitted());
    }

    public <T extends WorkflowDefinition> WorkflowDefinitionRepresentation getWorkflowDefinitionSimpleRepresentation(T workflowDefinition) {
        return new WorkflowDefinitionRepresentation().withId(workflowDefinition.getId());
    }
    
    public ActionCustomQuestionConfiguration getActionCustomQuestionConfiguration(ActionCustomQuestionConfigurationDTO configurationDTO) {
        String name = configurationDTO.getComponent();
        List<String> options = configurationDTO.getOptions();
        String validation = configurationDTO.getValidation();
        return new ActionCustomQuestionConfiguration().withCustomQuestionType(PrismCustomQuestionType.getByComponentName(name)).withName(name)
                .withEditable(configurationDTO.getEditable()).withIndex(configurationDTO.getIndex()).withLabel(configurationDTO.getLabel()).withDescription(configurationDTO.getDescription())
                .withOptions(options == null ? null : Joiner.on("|").join(options)).withRequired(configurationDTO.getRequired())
                .withValidation(validation).withWeighting(configurationDTO.getWeighting()).withPlaceholder(configurationDTO.getPlaceholder());
    }

}
