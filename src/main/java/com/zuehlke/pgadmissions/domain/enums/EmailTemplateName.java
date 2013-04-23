package com.zuehlke.pgadmissions.domain.enums;

public enum EmailTemplateName {

	REFEREE_NOTIFICATION("Referee notification"), //sent by timer and on event
	REFEREE_REMINDER("Referee reminder"),//sent by timer
	INTERVIEWER_NOTIFICATION("Interviewer notification"),//sent by timer
	NEW_PASSWORD_CONFIRMATION("New password confirmation"),//send on event
	REGISTRY_VALIDATION_REQUEST("Registry vlaidation request"),
	REGISTRATION_CONFIRMATION("Registration confirmation"), //sent on event
	EXPORT_ERROR("Export error"), //sent on event
	IMPORT_ERROR("Import error"), //sent on event
	REJECTED_NOTIFICATION("Rejected notification"),//sent on event
	MOVED_TO_APPROVED_NOTIFICATION("Moved to approved notification"),//sent by timer
	APPLICATION_SUBMIT_CONFIRMATION("Application submit confirmation"),//sent on event
	MOVED_TO_INTERVIEW_NOTIFICATION("Moved to interview notification"),//sent by timer
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
