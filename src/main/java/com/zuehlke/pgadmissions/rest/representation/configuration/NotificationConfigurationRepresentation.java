package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;

public class NotificationConfigurationRepresentation extends WorkflowConfigurationRepresentation {

    private String subject;

    private String content;

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

    public NotificationConfigurationRepresentation withProperty(PrismNotificationDefinition property) {
        setDefinitionId(property);
        return this;
    }

    public NotificationConfigurationRepresentation withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public NotificationConfigurationRepresentation withContent(String content) {
        this.content = content;
        return this;
    }

}
