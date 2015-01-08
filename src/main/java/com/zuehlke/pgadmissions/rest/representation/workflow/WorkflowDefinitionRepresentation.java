package com.zuehlke.pgadmissions.rest.representation.workflow;

public class WorkflowDefinitionRepresentation {

    private Enum<?> id;

    public final Enum<?> getId() {
        return id;
    }

    public final void setId(Enum<?> id) {
        this.id = id;
    }

    public WorkflowDefinitionRepresentation withId(Enum<?> id) {
        this.id = id;
        return this;
    }

}
