package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO;

public class WorkflowPropertyConfigurationConverter extends DozerConverter<WorkflowPropertyConfigurationValueDTO, WorkflowPropertyConfiguration> {

    public WorkflowPropertyConfigurationConverter() {
        super(WorkflowPropertyConfigurationValueDTO.class, WorkflowPropertyConfiguration.class);
    }

    @Override
    public WorkflowPropertyConfiguration convertTo(WorkflowPropertyConfigurationValueDTO source, WorkflowPropertyConfiguration destination) {
        boolean enabled = source.getEnabled();
        PrismWorkflowPropertyDefinition definitionId = source.getDefinitionId();

        boolean defineRange = definitionId.isDefineRange();
        int minimum = defineRange && enabled ? source.getMinimum() : 0;
        int maximum = defineRange && enabled ? source.getMaximum() : 0;

        boolean required = defineRange ? source.getMinimum() > 1 : definitionId.isCanBeOptional() ? true : source.getRequired();
        return new WorkflowPropertyConfiguration().withEnabled(enabled).withMinimum(minimum).withMaximum(maximum).withRequired(required).withActive(true);
    }

    @Override
    public WorkflowPropertyConfigurationValueDTO convertFrom(WorkflowPropertyConfiguration source, WorkflowPropertyConfigurationValueDTO destination) {
        throw new UnsupportedOperationException();
    }

}
