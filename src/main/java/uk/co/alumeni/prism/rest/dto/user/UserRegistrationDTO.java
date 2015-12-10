package uk.co.alumeni.prism.rest.dto.user;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;

public class UserRegistrationDTO extends UserDTO {

    private String activationCode;

    @Size(min = 8, max = 15)
    private String password;

    @Valid
    private CommentDTO comment;

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CommentDTO getComment() {
        return comment;
    }

    public void setComment(CommentDTO comment) {
        this.comment = comment;
    }

    public UserRegistrationDTO withFirstName(String firstName) {
        setFirstName(firstName);
        return this;
    }

    public UserRegistrationDTO withLastName(String lastName) {
        setLastName(lastName);
        return this;
    }

    public UserRegistrationDTO withEmail(String email) {
        setEmail(email);
        return this;
    }

    public UserRegistrationDTO withActivationCode(String activationCode) {
        this.activationCode = activationCode;
        return this;
    }

    public UserRegistrationDTO withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserRegistrationDTO withComment(CommentDTO comment) {
        this.comment = comment;
        return this;
    }

}
