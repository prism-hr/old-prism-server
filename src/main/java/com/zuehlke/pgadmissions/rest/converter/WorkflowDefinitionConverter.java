package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;

public class WorkflowDefinitionConverter extends DozerConverter<WorkflowDefinition, WorkflowDefinitionRepresentation> {

    public WorkflowDefinitionConverter() {
        super(WorkflowDefinition.class, WorkflowDefinitionRepresentation.class);
    }

    @Override
    public WorkflowDefinitionRepresentation convertTo(WorkflowDefinition source, WorkflowDefinitionRepresentation destination) {
        return source == null ? null : new WorkflowDefinitionRepresentation().withId(source.getId());
    }

    @Override
    public ActionCustomQuestionDefinition convertFrom(WorkflowDefinitionRepresentation source, WorkflowDefinition destination) {
        throw new UnsupportedOperationException();
    }

}
