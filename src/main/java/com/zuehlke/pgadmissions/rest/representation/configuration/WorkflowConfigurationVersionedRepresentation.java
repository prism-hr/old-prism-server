package com.zuehlke.pgadmissions.rest.representation.configuration;

public class WorkflowConfigurationVersionedRepresentation extends WorkflowConfigurationRepresentation {

    private Integer version;

    public final Integer getVersion() {
        return version;
    }

    public final void setVersion(Integer version) {
        this.version = version;
    }

}
