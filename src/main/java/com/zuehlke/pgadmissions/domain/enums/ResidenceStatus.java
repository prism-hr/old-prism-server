package com.zuehlke.pgadmissions.domain.enums;

public enum ResidenceStatus {

	HOME("Home/EU"), OVERSEAS("Overseas"), UNSURE("Unsure");

	private final String displayValue;

	private ResidenceStatus(String displayValue){	
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
	
}
