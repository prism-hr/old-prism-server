package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationFormStatus {
	
	UNSUBMITTED("Not Submitted"), VALIDATION("Validation"), REJECTED("Rejected"), APPROVAL("Approval"), APPROVED("Approved"), WITHDRAWN("Withdrawn");
	
	private final String displayValue;
	
	private ApplicationFormStatus(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}

	public static ApplicationFormStatus[] getAvailableNextStati(ApplicationFormStatus status) {
		if(status == VALIDATION){
			return new ApplicationFormStatus[]{REJECTED, APPROVAL} ;
		}
		return  new ApplicationFormStatus[]{} ;
	}

}
