package com.zuehlke.pgadmissions.domain.enums;

public enum EmailTemplateName {

	REFEREE_NOTIFICATION("Reference Request"),
	REFEREE_REMINDER("Reference Request Reminder"),//sent by timer
	INTERVIEWER_NOTIFICATION("Interview Confirmation - Staff"),
	INTERVIEW_VOTE_NOTIFICATION("Interview Scheduling Request"),
	INTERVIEW_VOTE_REMINDER("Interview Scheduling Reminder"),
	INTERVIEW_VOTE_CONFIRMATION("Interview Availability Confirmation"),
	NEW_PASSWORD_CONFIRMATION("New Password Confirmation"),//send on event
	REGISTRATION_CONFIRMATION("Confirmation of Registration"),
	EXPORT_ERROR("Export Error"), //sent on event
	IMPORT_ERROR("Import Error"), //sent on event
	REJECTED_NOTIFICATION("Rejected Notification"),//sent on event
	MOVED_TO_APPROVED_NOTIFICATION("Approved Notification"),
	APPLICATION_SUBMIT_CONFIRMATION("Receipt of Application"),//sent on event
	MOVED_TO_INTERVIEW_NOTIFICATION("Interview Confirmation - Applicant"),
	OPPORTUNITY_REQUEST_OUTCOME("Opportunity Request Outcome"),
	NEW_USER_SUGGESTION("New User Invitation"),
	DIGEST_UPDATE_NOTIFICATION("Update Notification"), 
	DIGEST_TASK_NOTIFICATION("Task Notification"),
	DIGEST_TASK_REMINDER("Task Reminder"),
	OPPORTUNITY_REQUEST_NOTIFICATION("Opportunity Request Notification");
	
	private final String displayValue;
	
	private EmailTemplateName(String displayValue) {
		this.displayValue=displayValue;
	}
	
	public String displayValue() {
		return displayValue;
	}
}
