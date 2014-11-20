package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowPropertyDefinitionRepresentation;

public class WorkflowPropertyDefinitionConverter extends DozerConverter<WorkflowPropertyDefinition, WorkflowPropertyDefinitionRepresentation> {

    public WorkflowPropertyDefinitionConverter() {
        super(WorkflowPropertyDefinition.class, WorkflowPropertyDefinitionRepresentation.class);
    }

    @Override
    public WorkflowPropertyDefinitionRepresentation convertTo(WorkflowPropertyDefinition source, WorkflowPropertyDefinitionRepresentation destination) {
        return source == null ? null : new WorkflowPropertyDefinitionRepresentation().withId(source.getId())
                .withRangeSpecification(source.getRangeSpecification()).withMinimumPermitted(source.getMinimumPermitted())
                .withMaximumPermitted(source.getMaximumPermitted());
    }

    @Override
    public WorkflowPropertyDefinition convertFrom(WorkflowPropertyDefinitionRepresentation source, WorkflowPropertyDefinition destination) {
        throw new UnsupportedOperationException();
    }

}
