package com.zuehlke.pgadmissions.rest.representation.workflow;

public class WorkflowPropertyDefinitionRepresentation extends StateDurationDefinitionRepresentation {
    
    private Boolean rangeSpecification;

    public final Boolean getRangeSpecification() {
        return rangeSpecification;
    }

    public final void setRangeSpecification(Boolean rangeSpecification) {
        this.rangeSpecification = rangeSpecification;
    }
    
    public WorkflowPropertyDefinitionRepresentation withId(Enum<?> id) {
        setId(id);
        return this;
    }
    
    public WorkflowPropertyDefinitionRepresentation withRangeSpecification(Boolean rangeSpecification) {
        this.rangeSpecification = rangeSpecification;
        return this;
    }
    
    public WorkflowPropertyDefinitionRepresentation withMinimumPermitted(Integer minimumPermitted) {
        setMinimumPermitted(minimumPermitted);
        return this;
    }
    
    public WorkflowPropertyDefinitionRepresentation withMaximumPermitted(Integer maximumPermitted) {
        setMaximumPermitted(maximumPermitted);
        return this;
    }

}
