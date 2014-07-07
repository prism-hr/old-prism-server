package com.zuehlke.pgadmissions.domain.definitions;

public enum ApplicationResidenceStatus {

	HOME("Home/EU"), OVERSEAS("Overseas"), UNSURE("Unsure");

	private final String displayValue;

	private ApplicationResidenceStatus(String displayValue){	
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
	
}
