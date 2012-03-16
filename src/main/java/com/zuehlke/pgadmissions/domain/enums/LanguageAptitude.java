package com.zuehlke.pgadmissions.domain.enums;

public enum LanguageAptitude {
	
	ELEMENTARY("Elementary"), LIMITED("Limited working proficiency"), PROFESSIONAL("Professional working proficiency"), FULL("Full professional proficiency"), NATIVE(
			"Native/multilingual proficiency");
	private final String displayValue;

	public String getDisplayValue() {
		return displayValue;
	}

	private LanguageAptitude(String displayValue) {
		this.displayValue = displayValue;
	}

}
