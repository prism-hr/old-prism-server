package com.zuehlke.pgadmissions.domain.enums;

public enum SearchCategories {
	
	APPLICATION_CODE("Application Code"),
	APPLICANT_NAME("Applicant Name"),
	PROGRAMME_NAME("Programme Name"),
	APPLICATION_STATUS("Application Status");
	
	private final String displayValue;

	private SearchCategories(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}
}
