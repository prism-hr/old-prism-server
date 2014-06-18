package com.zuehlke.pgadmissions.domain.enums;

public enum Gender {

	FEMALE("Female"), 
	MALE("Male"), 
	INDETERMINATE_GENDER("Indeterminate Gender");
	
	private final String displayValue;
	
	public String getDisplayValue() {
		return displayValue;
	}

	private Gender(String displayValue) {
		this.displayValue = displayValue;
	}
}
