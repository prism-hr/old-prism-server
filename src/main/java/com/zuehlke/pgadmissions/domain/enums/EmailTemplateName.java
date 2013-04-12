package com.zuehlke.pgadmissions.domain.enums;

public enum EmailTemplateName {

	REVIEWER_NOTIFICATION("Reviewer notification"),//updated
	REVIEWER_ASSIGNED_NOTIFICATION("Reviewer assigned notification"),//updated
	REVIEWER_REMINDER("Reviewer reminder"),//updated
	REFEREE_NOTIFICATION("Referee notification"),//updated
	REFEREE_REMINDER("Referee reminder"),//updated
	INTERVIEWER_NOTIFICATION("Interviewer notification"),//updated
	INTERVIEW_SUBMISSION_NOTIFICATION("Interview submission notification"),//update
	INTERVIEWER_REMINDER("Interviewer reminder"),//updated
	INTERVIEWER_REMINDER_FIRST("Interviewer reminder first"),//updated
	APPROVAL_NOTIFICATION("Approval notification"),//updated
	REJECTED_NOTIFICATION_ADMIN("Rejected notification admin"),//updated
	REVIEW_SUBMISSION_NOTIFICATION("Review submission notification"),//updated
	NEW_PASSWORD_CONFIRMATION("New password confirmation"),//updated
	SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION("Supervisor confirm supervision notification"),//updated
	SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER("Supervisor confirm supervision notification reminder"),//updated
	SUPERVISOR_NOTIFICATION("Supervisor notification"),//updated
	APPROVED_NOTIFICATION("Approved notification"),//updated
	APPLICATION_UPDATED_CONFIRMATION("Application updated confirmation"),//updated
	APPROVAL_RESTART_REQUEST_REMINDER("Approval restart request reminder"),//updated
	RESTART_APPROVAL_REQUEST("Restart approval request"),//updated
	REFERENCE_SUBMIT_CONFIRMATION("Reference submit confirmation"),//updated
	REFERENCE_RESPOND_CONFIRMATION("Reference respond confirmation"),//updated
	REGISTRY_VALIDATION_REQUEST("Registry vlaidation request"),//updated
	REGISTRATION_CONFIRMATION("Registration confirmation"),//updated
	EXPORT_ERROR("Export error"),//updated
	IMPORT_ERROR("Import error"),//updated
	REJECTED_NOTIFICATION("Rejected notification"),//updated
	APPLICATION_APPROVAL_REMINDER("Application approval reminder"),//updated
	APPLICATION_VALIDATION_REMINDER("Application validation reminder"),//updated
	MOVED_TO_APPROVED_NOTIFICATION("Moved to approved notification"),//updated
	MOVED_TO_REVIEW_NOTIFICATION("Moved to review notification"),//updated
	APPLICATION_SUBMIT_CONFIRMATION("Application submit confirmation"),//updated
	APPLICATION_SUBMIT_CONFIRMATION_ADMIN("Application submit confirmation admin"),//updated
	MOVED_TO_APPROVAL_NOTIFICATION("Moved to approval notification"),//updated
	MOVED_TO_INTERVIEW_NOTIFICATION("Moved to interview notification"),//updated
	APPLICATION_INTERVIEW_REMINDER_FIRST("Application interview reminder first"),//updated
	APPLICATION_INTERVIEW_REMINDER("Application interview reminder"),//updated
	APPLICATION_REVIEW_REMINDER_FIRST("Application review reminder first"),//updated
	APPLICATION_REVIEW_REMINDER("Application review reminder"),//updated
	APPLICATION_WITHDRAWN_NOTIFICATION("Application withdrawn notification"), //updated 
	NEW_USER_SUGGESTION("New user suggestion"), //ok
	REGISTER_REFEREE_CONFIRMATION("Register referee confirmation"),//ok
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
