package uk.co.alumeni.prism.rest.representation.configuration;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;

public class NotificationConfigurationRepresentation extends WorkflowConfigurationRepresentation {

    private String subject;

    private String content;

    private List<DocumentRepresentation> documents;

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

    public List<DocumentRepresentation> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentRepresentation> documents) {
        this.documents = documents;
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
