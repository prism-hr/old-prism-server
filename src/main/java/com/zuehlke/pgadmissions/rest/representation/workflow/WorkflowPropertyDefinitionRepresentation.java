package com.zuehlke.pgadmissions.rest.representation.workflow;

public class WorkflowPropertyDefinitionRepresentation extends StateDurationDefinitionRepresentation {

    private Boolean optional;
    
    private Boolean rangeSpecification;
    
    public final Boolean getOptional() {
        return optional;
    }

    public final void setOptional(Boolean optional) {
        this.optional = optional;
    }

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
    
    public WorkflowPropertyDefinitionRepresentation withOptional(Boolean optional) {
        this.optional = optional;
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
