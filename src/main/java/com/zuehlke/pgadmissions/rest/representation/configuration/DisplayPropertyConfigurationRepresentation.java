package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;

public class DisplayPropertyConfigurationRepresentation extends WorkflowConfigurationRepresentation implements
        PrismConfigurationRepresentationCategorizable<PrismDisplayPropertyCategory> {

	private PrismDisplayPropertyCategory category;

	public final PrismDisplayPropertyCategory getCategory() {
		return category;
	}

	public final void setCategory(PrismDisplayPropertyCategory category) {
		this.category = category;
	}

}
