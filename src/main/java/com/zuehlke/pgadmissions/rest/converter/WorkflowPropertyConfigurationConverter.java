package com.zuehlke.pgadmissions.rest.converter;

import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO;
import org.dozer.DozerConverter;

public class WorkflowPropertyConfigurationConverter extends DozerConverter<WorkflowPropertyConfigurationValueDTO, WorkflowPropertyConfiguration> {

    public WorkflowPropertyConfigurationConverter() {
        super(WorkflowPropertyConfigurationValueDTO.class, WorkflowPropertyConfiguration.class);
    }

    @Override
    public WorkflowPropertyConfiguration convertTo(WorkflowPropertyConfigurationValueDTO source, WorkflowPropertyConfiguration destination) {
        return new WorkflowPropertyConfiguration().withEnabled(source.getEnabled()).withMinimum(source.getMinimum()).withMaximum(source.getMaximum())
                .withRequired(source.getRequired()).withActive(true);
    }

    @Override
    public WorkflowPropertyConfigurationValueDTO convertFrom(WorkflowPropertyConfiguration source, WorkflowPropertyConfigurationValueDTO destination) {
        throw new UnsupportedOperationException();
    }

}
