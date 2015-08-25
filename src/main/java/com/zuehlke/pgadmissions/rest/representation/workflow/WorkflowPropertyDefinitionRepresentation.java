package com.zuehlke.pgadmissions.rest.representation.workflow;

public class WorkflowPropertyDefinitionRepresentation extends StateDurationDefinitionRepresentation {

    private Boolean defineRange;

    private Boolean canBeDisabled;

    private Boolean canBeOptional;

    public final Boolean getDefineRange() {
        return defineRange;
    }

    public final void setDefineRange(Boolean defineRange) {
        this.defineRange = defineRange;
    }

    public final Boolean getCanBeDisabled() {
        return canBeDisabled;
    }

    public final void setCanBeDisabled(Boolean canBeDisabled) {
        this.canBeDisabled = canBeDisabled;
    }

    public Boolean getCanBeOptional() {
        return canBeOptional;
    }

    public void setCanBeOptional(Boolean canBeOptional) {
        this.canBeOptional = canBeOptional;
    }

    public WorkflowPropertyDefinitionRepresentation withId(Enum<?> id) {
        setId(id);
        return this;
    }

    public WorkflowPropertyDefinitionRepresentation withDefineRange(Boolean defineRange) {
        this.defineRange = defineRange;
        return this;
    }

    public WorkflowPropertyDefinitionRepresentation withCanBeDisabled(Boolean canBeDisabled) {
        this.canBeDisabled = canBeDisabled;
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
