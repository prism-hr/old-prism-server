package com.zuehlke.pgadmissions.domain.enums;

public enum EmailTemplateName {

	REVIEWER_NOTIFICATION("Reviewer notification"),
	REVIEWER_ASSIGNED_NOTIFICATION("Reviewer assigned notification"),
	REVIEWER_REMINDER("Reviewer reminder"),
	REFEREE_NOTIFICATION("Referee notification"),
	REFEREE_REMINDER("Referee reminder"),
	INTERVIEWER_NOTIFICATION("Interviewer notification"),
	INTERVIEW_SUBMISSION_NOTIFICATION("Interview submission notification"),
	INTERVIEWER_REMINDER("Interviewer reminder"),
	INTERVIEWER_REMINDER_FIRST("Interviewer reminder first"),
	APPROVAL_NOTIFICATION("Approval notification"),
	REJECTED_NOTIFICATION_ADMIN("Rejected notification admin"),
	REVIEW_SUBMISSION_NOTIFICATION("Review submission notification"),
	NEW_PASSWORD_CONFIRMATION("New password confirmation"),
	SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION("Supervisor confirm supervision notification"),
	SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER("Supervisor confirm supervision notification reminder"),
	SUPERVISOR_NOTIFICATION("Supervisor notification"),
	APPROVED_NOTIFICATION("Approved notification"),
	APPLICATION_UPDATED_CONFIRMATION("Application updated confirmation"),
	APPROVAL_RESTART_REQUEST_REMINDER("Approval restart request reminder"),
	RESTART_APPROVAL_REQUEST("Restart approval request"),
	REFERENCE_SUBMIT_CONFIRMATION("Reference submit confirmation"),
	REFERENCE_RESPOND_CONFIRMATION("Reference respond confirmation"),
	REGISTRY_VALIDATION_REQUEST("Registry vlaidation request"),
	REGISTRATION_CONFIRMATION("Registration confirmation"),
	EXPORT_ERROR("Export error"),
	IMPORT_ERROR("Import error"),
	REJECTED_NOTIFICATION("Rejected notification"),
	APPLICATION_APPROVAL_REMINDER("Application approval reminder"),
	APPLICATION_VALIDATION_REMINDER("Application validation reminder"),
	MOVED_TO_APPROVED_NOTIFICATION("Moved to approved notification"),
	MOVED_TO_REVIEW_NOTIFICATION("Moved to review notification"),
	APPLICATION_SUBMIT_CONFIRMATION("Application submit confirmation"),
	APPLICATION_SUBMIT_CONFIRMATION_ADMIN("Application submit confirmation admin"),
	MOVED_TO_APPROVAL_NOTIFICATION("Moved to approval notification"),
	MOVED_TO_INTERVIEW_NOTIFICATION("Moved to interview notification"),
	APPLICATION_INTERVIEW_REMINDER_FIRST("Application interview reminder first"),
	APPLICATION_INTERVIEW_REMINDER("Application interview reminder"),
	APPLICATION_REVIEW_REMINDER_FIRST("Application review reminder first"),
	APPLICATION_REVIEW_REMINDER("Application review reminder"),
	APPLICATION_WITHDRAWN_NOTIFICATION("Application withdrawn notification"),  
	NEW_USER_SUGGESTION("New user suggestion"),
	REGISTER_REFEREE_CONFIRMATION("Register referee confirmation"),
	INTERVIEW_ADMINISTRATION_REMINDER("Interview administration reminder"), 
	
	DIGEST("Digest"), 
	DIGEST_REMINDER("Digest Reminder");
	
	private final String displayValue;
	
	private EmailTemplateName(String displayValue) {
		this.displayValue=displayValue;
	}
	
	public String displayValue() {
		return displayValue;
	}
}
