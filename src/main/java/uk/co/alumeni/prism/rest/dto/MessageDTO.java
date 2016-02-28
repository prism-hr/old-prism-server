package uk.co.alumeni.prism.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.dto.user.UserEmailDTO;

import javax.validation.constraints.Size;
import java.util.List;

public class MessageDTO {

    @Size(max = 255)
    private String subject;

    @NotEmpty
    private String content;

    private List<UserEmailDTO> recipientUsers;

    private List<PrismRole> recipientRoles;

    private List<DocumentDTO> documents;

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

    public List<UserEmailDTO> getRecipientUsers() {
        return recipientUsers;
    }

    public void setRecipientUsers(List<UserEmailDTO> recipientUsers) {
        this.recipientUsers = recipientUsers;
    }

    public List<PrismRole> getRecipientRoles() {
        return recipientRoles;
    }

    public void setRecipientRoles(List<PrismRole> recipientRoles) {
        this.recipientRoles = recipientRoles;
    }

    public List<DocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDTO> documents) {
        this.documents = documents;
    }

}
