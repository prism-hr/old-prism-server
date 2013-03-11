package com.zuehlke.pgadmissions.domain.enums;

public enum EmailTemplateName {

	REVIEWER_NOTIFICATION("Reviewer notification"),
	REVIEWER_REMINDER("Reviewer reminder"),
	REFEREE_NOTIFICATION("Referee notification"),
	REFEREE_REMINDER("Rferee reminder"),
	INTERVIEWER_REMINDER("Interviewer reminder"),
	INTERVIEWER_REMINDER_FIRST("Interviewer reminder first"),
	APPROVAL_NOTIFICATION("Approval notification"),
	NEW_PASSWORD_CONFIRMATION("New password confirmation"),
	SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION("Supervisor confirm supervision notification"),
	SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER("Supervisor confirm supervision notification reminder"),
	SUPERVISOR_NOTIFICATION("Supervisor notification");
	
	private final String displayValue;
	
	private EmailTemplateName(String displayValue) {
		this.displayValue=displayValue;
	}
	
	public String displayValue() {
		return displayValue;
	}
}
