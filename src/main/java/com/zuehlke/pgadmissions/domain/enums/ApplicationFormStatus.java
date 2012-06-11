package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationFormStatus {
	// attention: order of the enum values is also the logical ordering of application-status
	UNSUBMITTED("Not Submitted"), // 
	VALIDATION("Validation"), // 
	REVIEW("Review"), //
	INTERVIEW("Interview"), //
	APPROVAL("Approval"), //
	APPROVED("Approved"), //
	WITHDRAWN("Withdrawn"), //
	REJECTED("Rejected"); //

	private final String displayValue;

	private ApplicationFormStatus(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}

	public static ApplicationFormStatus[] getAvailableNextStati(ApplicationFormStatus status) {
		if (status == VALIDATION || status == REVIEW) {
			return new ApplicationFormStatus[] { REJECTED, REVIEW, APPROVAL, INTERVIEW };
		}
		if (status == INTERVIEW) {
			return new ApplicationFormStatus[] { REJECTED, APPROVAL, INTERVIEW };
		}
		return new ApplicationFormStatus[] {};
	}

	public static ApplicationFormStatus[] getConfigurableStages() {
		return new ApplicationFormStatus[] { VALIDATION, REVIEW, APPROVAL, INTERVIEW };
	}

}
