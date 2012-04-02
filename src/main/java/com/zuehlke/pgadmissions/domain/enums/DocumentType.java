package com.zuehlke.pgadmissions.domain.enums;

public enum DocumentType {

	CV("CV / resume"), PERSONAL_STATEMENT("Personal Statement"), REFERENCE("Reference"), SUPPORTING_FUNDING("Supporting documentation for funding");
	private String displayValue;

	public String getDisplayValue() {
		return displayValue;
	}

	private DocumentType(String displayValue) {
		this.displayValue = displayValue;
	}
}