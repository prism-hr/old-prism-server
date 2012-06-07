package com.zuehlke.pgadmissions.domain.enums;

public enum SearchCategory {
	
	APPLICATION_NUMBER("Application number"),
	APPLICANT_NAME("Applicant"),
	PROGRAMME_NAME("Programme"),
	APPLICATION_STATUS("Status"),
	APPLICATION_DATE("Date");
	
	private final String displayValue;

	private SearchCategory(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {
		return displayValue;
	}
}
