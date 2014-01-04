package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationFormStatus {
	
    UNSUBMITTED("Not Submitted"),
	VALIDATION("Validation"),
	REVIEW("Review"),
	INTERVIEW("Interview"), 
	APPROVAL("Approval"),
	APPROVED("Offer Recommended"),
	WITHDRAWN("Withdrawn"),
	REJECTED("Rejected");

	private final String displayValue;

	private ApplicationFormStatus(final String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {
		return displayValue;
	}
	
}