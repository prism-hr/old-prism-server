package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationFormStatus {
	
	UNSUBMITTED("Not Submitted"), VALIDATION("Validation"), REJECTED("Rejected"), APPROVED("Approved"), WITHDRAWN("Withdrawn");
	
	private final String displayValue;
	
	private ApplicationFormStatus(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}

}
