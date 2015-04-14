package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;

public class DisplayPropertyConfigurationRepresentation extends WorkflowConfigurationRepresentation {

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public DisplayPropertyConfigurationRepresentation withDefinitionId(PrismDisplayPropertyDefinition definitionId) {
		super.setDefinitionId(definitionId);
		return this;
	}
	
	public DisplayPropertyConfigurationRepresentation withValue(String value) {
		this.value = value;
		return this;
	}

}
