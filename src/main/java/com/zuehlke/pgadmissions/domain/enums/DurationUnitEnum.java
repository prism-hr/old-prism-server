package com.zuehlke.pgadmissions.domain.enums;

public enum DurationUnitEnum {
	MINUTES("Minutes"), 
	HOURS("Hours"), 
	DAYS("Days"), 
	WEEKS("Weeks");
	
	private final String displayValue;

	private DurationUnitEnum(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}

}
