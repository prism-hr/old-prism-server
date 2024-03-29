package uk.co.alumeni.prism.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class NotificationConfigurationDTO extends WorkflowConfigurationDTO {

    @NotNull
    private PrismNotificationDefinition definitionId;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String content;

    @Valid
    private List<DocumentDTO> documents;

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

    public List<DocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDTO> documents) {
        this.documents = documents;
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
