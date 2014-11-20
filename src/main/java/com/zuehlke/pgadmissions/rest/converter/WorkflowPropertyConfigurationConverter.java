package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO;

public class WorkflowPropertyConfigurationConverter extends DozerConverter<WorkflowPropertyConfigurationValueDTO, WorkflowPropertyConfiguration> {

    public WorkflowPropertyConfigurationConverter() {
        super(WorkflowPropertyConfigurationValueDTO.class, WorkflowPropertyConfiguration.class);
    }

    @Override
    public WorkflowPropertyConfiguration convertTo(WorkflowPropertyConfigurationValueDTO source, WorkflowPropertyConfiguration destination) {
        return new WorkflowPropertyConfiguration().withEnabled(source.getEnabled()).withMinimum(source.getMinimum()).withMaximum(source.getMaximum())
                .withActive(true);
    }

    @Override
    public WorkflowPropertyConfigurationValueDTO convertFrom(WorkflowPropertyConfiguration source, WorkflowPropertyConfigurationValueDTO destination) {
        throw new UnsupportedOperationException();
    }

}
