package com.zuehlke.pgadmissions.domain.enums;

public enum YesNoUnsureResponse {
	YES("Yes"), NO("No"), UNSURE("Unsure");
	
	private final String displayValue;

	private YesNoUnsureResponse(String displayValue){	
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
}
