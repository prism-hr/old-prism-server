package com.zuehlke.pgadmissions.domain.enums;

public enum Gender {

	FEMALE("Female"), 
	MALE("Male"), 
	PREFER_NOT_TO_SAY("Prefer not to say");
	
	private final String displayValue;
	
	public String getDisplayValue() {
		return displayValue;
	}

	private Gender(String displayValue) {
		this.displayValue = displayValue;
	}
}
