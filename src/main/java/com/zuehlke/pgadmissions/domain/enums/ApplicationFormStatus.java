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
		if (status == VALIDATION || status == REVIEW ) {
			return new ApplicationFormStatus[] { REJECTED, REVIEW, APPROVAL, INTERVIEW };
		}
		if( status == INTERVIEW){
			return new ApplicationFormStatus[] { REJECTED, APPROVAL, INTERVIEW };
		}
		return new ApplicationFormStatus[] {};
	}
	
	public static ApplicationFormStatus[] getConfigurableStages() {
			return new ApplicationFormStatus[] { VALIDATION, REVIEW, APPROVAL, INTERVIEW };
	}

}
