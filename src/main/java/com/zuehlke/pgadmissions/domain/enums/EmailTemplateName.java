package com.zuehlke.pgadmissions.domain.enums;

public enum EmailTemplateName {

	REVIEWER_NOTIFICATION("Reviewer notification"),
	REVIEWER_ASSIGNED_NOTIFICATION("Reviewer assigned notification"),
	REVIEWER_REMINDER("Reviewer reminder"),
	REFEREE_NOTIFICATION("Referee notification"),
	REFEREE_REMINDER("Rferee reminder"),
	INTERVIEWER_NOTIFICATION("Interviewer notification"),
	INTERVIEW_SUBMISSION_NOTIFICATION("Interview submission notification"),
	INTERVIEWER_REMINDER("Interviewer reminder"),
	INTERVIEWER_REMINDER_FIRST("Interviewer reminder first"),
	APPROVAL_NOTIFICATION("Approval notification"),
	REJECTED_NOTIFICATION("Rejected notification"),
	REVIEW_SUBMISSION_NOTIFICATION("Review submission notification"),
	REJECTED_NOTIFICATION_ADMIN("Rejected notification admin"),
	NEW_PASSWORD_CONFIRMATION("New password confirmation"),
	REGISTER_REFEREE_CONFIRMATION("Register referee confirmation"),
	SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION("Supervisor confirm supervision notification"),
	SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER("Supervisor confirm supervision notification reminder"),
	APPLICATION_APPROVAL_REMINDER("Application approval reminder"),
	APPROVAL_RESTART_REQUEST_REMINDER("Approval restart request reminder"),
	APPROVED_NOTIFICATION("Approved notification"),
	APPLICATION_SUBMIT_CONFIRMATION("Application submit confirmation"),
	APPLICATION_VALIDATION_REMINDER("Application validation reminder"),
	APPLICATION_UPDATED_CONFIRMATION("Application updated confirmation"),
	APPLICATION_SUBMIT_CONFIRMATION_ADMIN("Application submit confirmation admin"),
	REFERENCE_RESPOND_CONFIRMATION("Reference respond confirmation"),
	REFERENCE_SUBMIT_CONFIRMATION("Reference submit confirmation"),
	REGISTRY_VALIDATION_REQUEST("Registry vlaidation request"),
	RESTART_APPROVAL_REQUEST("Restart approval request"),
	REGISTRATION_CONFIRMATION("Registration confirmation"),
	MOVED_TO_APPROVAL_NOTIFICATION("Moved to approval notification"),
	MOVED_TO_APPROVED_NOTIFICATION("Moved to approved notification"),
	MOVED_TO_INTERVIEW_NOTIFICATION("Moved to interview notification"),
	MOVED_TO_REVIEW_NOTIFICATION("Moved to review notification"),
	EXPORT_ERROR("Export error"),
	IMPORT_ERROR("Import error"),
	APPLICATION_INTERVIEW_REMINDER_FIRST("Application interview reminder first"),
	APPLICATION_INTERVIEW_REMINDER("Application interview reminder"),
	APPLICATION_REVIEW_REMINDER_FIRST("Application review reminder first"),
	APPLICATION_REVIEW_REMINDER("Application review reminder"),
	SUPERVISOR_NOTIFICATION("Supervisor notification");
	
	private final String displayValue;
	
	private EmailTemplateName(String displayValue) {
		this.displayValue=displayValue;
	}
	
	public String displayValue() {
		return displayValue;
	}
}
