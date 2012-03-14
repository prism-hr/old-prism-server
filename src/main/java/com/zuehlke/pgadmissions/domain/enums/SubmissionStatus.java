package com.zuehlke.pgadmissions.domain.enums;

public enum SubmissionStatus {

	UNSUBMITTED("Not Submitted"), SUBMITTED("Submitted");
	
	private final String displayValue;
	
	private SubmissionStatus(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}

}
