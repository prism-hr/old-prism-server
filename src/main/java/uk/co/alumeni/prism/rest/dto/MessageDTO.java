package uk.co.alumeni.prism.rest.dto;

import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.dto.user.UserEmailDTO;

public class MessageDTO {

    private Integer id;

    @NotEmpty
    @Size(max = 255)
    private String subject;

    @NotEmpty
    private String content;

    private List<UserEmailDTO> recipientUsers;

    private List<PrismRole> recipientRoles;

    private List<DocumentDTO> documents;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
