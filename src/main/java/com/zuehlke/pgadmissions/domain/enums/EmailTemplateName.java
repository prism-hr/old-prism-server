package com.zuehlke.pgadmissions.domain.enums;

public enum EmailTemplateName {

	REVIEWER_NOTIFICATION("Reviewer notification"),//sent by timer
	REVIEWER_ASSIGNED_NOTIFICATION("Reviewer assigned notification"),//sent by timer
	REVIEWER_REMINDER("Reviewer reminder"),//sent by timer
	REFEREE_NOTIFICATION("Referee notification"), //sent by timer and on event
	REFEREE_REMINDER("Referee reminder"),//sent by timer
	INTERVIEWER_NOTIFICATION("Interviewer notification"),//sent by timer
	INTERVIEW_SUBMISSION_NOTIFICATION("Interview submission notification"),//sent by timer
	INTERVIEWER_REMINDER("Interviewer reminder"),//sent by timer
	INTERVIEWER_REMINDER_FIRST("Interviewer reminder first"),//sent by timer
	APPROVAL_NOTIFICATION("Approval notification"),//sent by timer
	REJECTED_NOTIFICATION_ADMIN("Rejected notification admin"),//sent by timer
	REVIEW_SUBMISSION_NOTIFICATION("Review submission notification"),//sent by timer
	NEW_PASSWORD_CONFIRMATION("New password confirmation"),//send on event
	SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION("Supervisor confirm supervision notification"),//sent by timer
	SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER("Supervisor confirm supervision notification reminder"),//sent by timer
	SUPERVISOR_NOTIFICATION("Supervisor notification"),//sent by timer
	APPROVED_NOTIFICATION("Approved notification"),//sent by timer
	APPLICATION_UPDATED_CONFIRMATION("Application updated confirmation"),//sent by timer
	APPROVAL_RESTART_REQUEST_REMINDER("Approval restart request reminder"),//sent by timer
	RESTART_APPROVAL_REQUEST("Restart approval request"),//sent by timer
	REFERENCE_SUBMIT_CONFIRMATION("Reference submit confirmation"),//sent on event
	REFERENCE_RESPOND_CONFIRMATION("Reference respond confirmation"),
	REGISTRY_VALIDATION_REQUEST("Registry vlaidation request"),
	REGISTRATION_CONFIRMATION("Registration confirmation"), //sent on event
	EXPORT_ERROR("Export error"), //sent on event
	IMPORT_ERROR("Import error"), //sent on event
	REJECTED_NOTIFICATION("Rejected notification"),//sent by timer
	APPLICATION_APPROVAL_REMINDER("Application approval reminder"),//sent by timer
	APPLICATION_VALIDATION_REMINDER("Application validation reminder"),//sent by timer
	MOVED_TO_APPROVED_NOTIFICATION("Moved to approved notification"),//sent by timer
	MOVED_TO_REVIEW_NOTIFICATION("Moved to review notification"),//sent by timer
	APPLICATION_SUBMIT_CONFIRMATION("Application submit confirmation"),//sent by timer
	APPLICATION_SUBMIT_CONFIRMATION_ADMIN("Application submit confirmation admin"),//sent by timer
	MOVED_TO_APPROVAL_NOTIFICATION("Moved to approval notification"),//sent by timer
	MOVED_TO_INTERVIEW_NOTIFICATION("Moved to interview notification"),//sent by timer
	APPLICATION_INTERVIEW_REMINDER_FIRST("Application interview reminder first"),
	APPLICATION_INTERVIEW_REMINDER("Application interview reminder"),//sent by timer
	APPLICATION_REVIEW_REMINDER_FIRST("Application review reminder first"),//sent by timer
	APPLICATION_REVIEW_REMINDER("Application review reminder"),//sent by timer
	APPLICATION_WITHDRAWN_NOTIFICATION("Application withdrawn notification"), //deprecated 
	NEW_USER_SUGGESTION("New user suggestion"),//sent by timer
	REGISTER_REFEREE_CONFIRMATION("Register referee confirmation"),//sent by timer
	INTERVIEW_ADMINISTRATION_REMINDER("Interview administration reminder"), //sent on event
	DIGEST_UPDATE_NOTIFICATION("Digest Update Notification"), 
	DIGEST_TASK_NOTIFICATION("Digest Task Notification"),
	DIGEST_TASK_REMINDER("Digest Task Reminder");
	
	private final String displayValue;
	
	private EmailTemplateName(String displayValue) {
		this.displayValue=displayValue;
	}
	
	public String displayValue() {
		return displayValue;
	}
}
