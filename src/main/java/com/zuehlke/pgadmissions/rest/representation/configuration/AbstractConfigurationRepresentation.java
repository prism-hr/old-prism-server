package com.zuehlke.pgadmissions.rest.representation.configuration;

public class AbstractConfigurationRepresentation {

    private Enum<?> definitionId;

    public Enum<?> getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(Enum<?> definitionId) {
        this.definitionId = definitionId;
    }

}
