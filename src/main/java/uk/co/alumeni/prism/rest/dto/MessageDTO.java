package uk.co.alumeni.prism.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;

import javax.validation.constraints.Size;
import java.util.List;

public class MessageDTO {

    @Size(max = 255)
    private String subject;

    @NotEmpty
    private String content;

    private List<UserDTO> participantUsers;

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

    public List<UserDTO> getParticipantUsers() {
        return participantUsers;
    }

    public void setParticipantUsers(List<UserDTO> participantUsers) {
        this.participantUsers = participantUsers;
    }

    public List<DocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDTO> documents) {
        this.documents = documents;
    }

}
