package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.PrismWorkflowConfiguration;

public class AbstractConfigurationRepresentation {

    private Enum<?> definitionId;

    private PrismWorkflowConfiguration configurationType;

    public Enum<?> getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(Enum<?> definitionId) {
        this.definitionId = definitionId;
    }

    public PrismWorkflowConfiguration getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(PrismWorkflowConfiguration configurationType) {
        this.configurationType = configurationType;
    }
}
