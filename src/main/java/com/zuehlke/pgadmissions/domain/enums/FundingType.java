package com.zuehlke.pgadmissions.domain.enums;

public enum FundingType {

	SCHOLARSHIP("Scholarship/Grant"), 
	EMPLOYER("Employer"), 
	SPONSOR("Industrial sponsor");

	private final String displayValue;

	private FundingType(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

}