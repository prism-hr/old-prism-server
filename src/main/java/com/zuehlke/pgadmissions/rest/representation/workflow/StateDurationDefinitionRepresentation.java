package com.zuehlke.pgadmissions.rest.representation.workflow;

public class StateDurationDefinitionRepresentation extends WorkflowDefinitionRepresentation {

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    public final Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public final void setMinimumPermitted(Integer minimumPermitted) {
        this.minimumPermitted = minimumPermitted;
    }

    public final Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public final void setMaximumPermitted(Integer maximumPermitted) {
        this.maximumPermitted = maximumPermitted;
    }

    public StateDurationDefinitionRepresentation withId(Enum<?> id) {
        setId(id);
        return this;
    }

    public StateDurationDefinitionRepresentation withMinimumPermitted(Integer minimumPermitted) {
        setMinimumPermitted(minimumPermitted);
        return this;
    }

    public StateDurationDefinitionRepresentation withMaximumPermitted(Integer maximumPermitted) {
        setMaximumPermitted(maximumPermitted);
        return this;
    }

}
