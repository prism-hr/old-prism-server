package com.zuehlke.pgadmissions.rest.dto;

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

}
