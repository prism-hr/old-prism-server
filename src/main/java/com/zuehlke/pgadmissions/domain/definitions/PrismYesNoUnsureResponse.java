package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismYesNoUnsureResponse {
	YES("Yes"), NO("No"), UNSURE("Unsure");
	
	private final String displayValue;

	private PrismYesNoUnsureResponse(String displayValue){	
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
}
