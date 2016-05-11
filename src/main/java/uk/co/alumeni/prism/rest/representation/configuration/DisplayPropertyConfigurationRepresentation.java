package uk.co.alumeni.prism.rest.representation.configuration;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;

public class DisplayPropertyConfigurationRepresentation extends WorkflowConfigurationRepresentation {

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DisplayPropertyConfigurationRepresentation withProperty(PrismDisplayPropertyDefinition property) {
		super.setDefinitionId(property);
		return this;
	}

	public DisplayPropertyConfigurationRepresentation withValue(String value) {
		this.value = value;
		return this;
	}

}
