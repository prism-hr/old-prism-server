package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition;

public class StateDurationConfigurationRepresentation extends WorkflowConfigurationRepresentation {

    private Integer duration;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public StateDurationConfigurationRepresentation withProperty(PrismStateDurationDefinition property) {
        setProperty(property);
        return this;
    }

    public StateDurationConfigurationRepresentation withDuration(Integer duration) {
        this.duration = duration;
        return this;
    }

}
