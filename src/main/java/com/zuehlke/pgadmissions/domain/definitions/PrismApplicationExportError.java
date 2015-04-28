package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO_EXPORT_PROGRAM_INSTANCE;

public enum PrismApplicationExportError {

	NO_EXPORT_PROGRAM_INSTANCE(SYSTEM_NO_EXPORT_PROGRAM_INSTANCE);

	private PrismDisplayPropertyDefinition classification;

	private PrismApplicationExportError(PrismDisplayPropertyDefinition classification) {
		this.classification = classification;
	}

	public PrismDisplayPropertyDefinition getClassification() {
		return classification;
	}

}
