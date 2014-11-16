package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.WorkflowResourceConfigurationType;

public class AbstractConfigurationRepresentation {

    private Enum<?> definitionId;

    private WorkflowResourceConfigurationType configurationType;

    public Enum<?> getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(Enum<?> definitionId) {
        this.definitionId = definitionId;
    }

    public WorkflowResourceConfigurationType getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(WorkflowResourceConfigurationType configurationType) {
        this.configurationType = configurationType;
    }
}
