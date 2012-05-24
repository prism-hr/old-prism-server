package com.zuehlke.pgadmissions.domain.enums;

public enum ValidationQuestionOptions {
	YES("Yes"), NO("No"), UNSURE("Unsure");
	
	private final String displayValue;

	private ValidationQuestionOptions(String displayValue){	
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
}
