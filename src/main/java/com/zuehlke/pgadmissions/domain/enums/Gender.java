package com.zuehlke.pgadmissions.domain.enums;

public enum Gender {

	FEMALE("Female"), 
	MALE("Male"), 
	INDETERMINATE("Indeterminate"); 
	
	private final String displayValue;
	
	public String getDisplayValue() {
		return displayValue;
	}

	private Gender(String displayValue) {
		this.displayValue = displayValue;
	}
}
