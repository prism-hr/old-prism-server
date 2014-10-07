package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

public class NotificationConfigurationDTO {

    @NotEmpty
    private String subject;

    @NotEmpty
    private String content;

    @Min(1)
    @Max(999)
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
