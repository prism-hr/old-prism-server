package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationFormStatusType {
	
	EDITABLEBYAPPLICANT(new ApplicationFormStatus[]{ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.REVIEW, 
			ApplicationFormStatus.UNSUBMITTED, ApplicationFormStatus.VALIDATION}),
			
	UNDERCONSIDERATION(new ApplicationFormStatus[]{ApplicationFormStatus.APPROVAL, ApplicationFormStatus.INTERVIEW, 
			ApplicationFormStatus.REVIEW, ApplicationFormStatus.VALIDATION}),
	
	EDITABLEBYSTAFF(new ApplicationFormStatus[]{ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.REVIEW}),
			
	COMPLETED(new ApplicationFormStatus[]{ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED, 
			ApplicationFormStatus.WITHDRAWN});
	
	private final ApplicationFormStatus[] states;
	
	private ApplicationFormStatusType(ApplicationFormStatus... states) {
		this.states = states;
	}

	public ApplicationFormStatus[] states() {
		return states;
	}
	
}