package com.zuehlke.pgadmissions.domain.enums;

public enum DocumentType {

	CV("CV / resume"), PERSONAL_STATEMENT("Personal Statement"), SUPPORTING_CANDIDATE_NATIONALITY("Supporting documentation for nationality"), SUPPORTING_MATERNAL_NATIONALITY(
			"Supporting documentation for maternal guardian's nationality"), SUPPORTING_PATERNAL_NATIONALITY(
			"Supporting documentation for paternal guardian's nationality"), SUPPORTING_ADDRESS("Supporting documentation for address / residency period"), SUPPORTING_EMPLOYMENT(
			"Supporting documentation for employment"), SUPPORTING_FUNDING("Supporting documentation for funding"), SUPPORTING_QUALIFICATION(
			"Supporting documentation for qualification"), REFERENCE("Reference");
	private String displayValue;

	public String getDisplayValue() {
		return displayValue;
	}

	private DocumentType(String displayValue) {
		this.displayValue = displayValue;
	}
}