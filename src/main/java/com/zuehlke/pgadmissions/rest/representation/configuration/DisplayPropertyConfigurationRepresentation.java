package com.zuehlke.pgadmissions.rest.representation.configuration;

public class DisplayPropertyConfigurationRepresentation extends WorkflowConfigurationRepresentation {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
