package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;

public class NotificationConfigurationDTO extends WorkflowConfigurationDTO {

    @NotNull
    private PrismNotificationDefinition definitionId;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String content;

    @Min(1)
    @Max(999)
    private Integer reminderInterval;

    @Override
    public PrismNotificationDefinition getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(PrismNotificationDefinition definitionId) {
        this.definitionId = definitionId;
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

    public NotificationConfigurationDTO withId(PrismNotificationDefinition id) {
        setDefinitionId(id);
        return this;
    }

    public NotificationConfigurationDTO withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public NotificationConfigurationDTO withContent(String content) {
        this.content = content;
        return this;
    }

    public NotificationConfigurationDTO withReminderInterval(Integer reminderInterval) {
        this.reminderInterval = reminderInterval;
        return this;
    }

}
