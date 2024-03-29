package uk.co.alumeni.prism.rest.representation.configuration;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition;

public class StateDurationConfigurationRepresentation extends WorkflowConfigurationRepresentation {

    private Integer duration;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public StateDurationConfigurationRepresentation withProperty(PrismStateDurationDefinition property) {
        setDefinitionId(property);
        return this;
    }

    public StateDurationConfigurationRepresentation withDuration(Integer duration) {
        this.duration = duration;
        return this;
    }

}
