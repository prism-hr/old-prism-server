package com.zuehlke.pgadmissions.domain.enums;

public enum SearchCategories {
	
	APPLICATION_CODE("Application Code"),
	APPLICANT_NAME("Applicant Name"),
	PROGRAMME_NAME("Programme Name");
	
	private final String displayValue;

	private SearchCategories(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}
}
