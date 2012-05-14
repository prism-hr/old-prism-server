package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationFormStatus {

	UNSUBMITTED("Not Submitted"), // 
	VALIDATION("Validation"), // 
	REJECTED("Rejected"), //
	APPROVAL("Approval"), //
	APPROVED("Approved"), //
	WITHDRAWN("Withdrawn"), //
	INTERVIEW("Interview"), //
	REVIEW("Review");

	private final String displayValue;

	private ApplicationFormStatus(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}

	public static ApplicationFormStatus[] getAvailableNextStati(ApplicationFormStatus status) {
		if (status == VALIDATION || status == REVIEW || status == INTERVIEW) {
			return new ApplicationFormStatus[] { REJECTED, REVIEW, APPROVAL, INTERVIEW };
		}
		return new ApplicationFormStatus[] {};
	}

}
