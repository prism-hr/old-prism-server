package com.zuehlke.pgadmissions.rest.representation.configuration;

public class NotificationConfigurationRepresentation extends WorkflowConfigurationRepresentation {

    private String label;

    private String tooltip;

    private String subject;

    private String content;

    private Integer reminderInterval;

    public final String getLabel() {
        return label;
    }

    public final void setLabel(String label) {
        this.label = label;
    }

    public final String getTooltip() {
        return tooltip;
    }

    public final void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

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
