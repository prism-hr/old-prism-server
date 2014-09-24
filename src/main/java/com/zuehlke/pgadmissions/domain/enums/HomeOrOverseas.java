package com.zuehlke.pgadmissions.domain.enums;

public enum HomeOrOverseas {

	HOME("Home/EU"), OVERSEAS("Overseas"), UNSURE("Unsure");

	private final String displayValue;

	private HomeOrOverseas(String displayValue){	
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
	
}