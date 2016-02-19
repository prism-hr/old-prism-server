package uk.co.alumeni.prism.rest.dto.message;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.dto.DocumentDTO;
import uk.co.alumeni.prism.rest.dto.user.UserEmailDTO;

public class MessageDTO {

    @Valid
    @NotNull
    private UserEmailDTO user;

    private String content;

    private List<UserEmailDTO> recipientUsers;

    private List<PrismRole> recipientRoles;

    private List<DocumentDTO> documents;

    public UserEmailDTO getUser() {
        return user;
    }

    public void setUser(UserEmailDTO user) {
        this.user = user;
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
