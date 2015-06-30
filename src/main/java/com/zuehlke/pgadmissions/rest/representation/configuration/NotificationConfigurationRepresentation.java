package com.zuehlke.pgadmissions.rest.representation.configuration;

public class NotificationConfigurationRepresentation extends WorkflowConfigurationRepresentation {

	private String subject;

	private String content;

	private Integer reminderInterval;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getReminderInterval() {
		return reminderInterval;
	}

	public void setReminderInterval(Integer reminderInterval) {
		this.reminderInterval = reminderInterval;
	}

}
