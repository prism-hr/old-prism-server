package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.WorkflowResourceConfigurationType;

public class AbstractConfigurationRepresentation {

    private WorkflowResourceConfigurationType configurationType;

    public WorkflowResourceConfigurationType getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(WorkflowResourceConfigurationType configurationType) {
        this.configurationType = configurationType;
    }
}
