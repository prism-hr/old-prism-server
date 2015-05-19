package com.zuehlke.pgadmissions.rest.dto.user;

import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserRegistrationDTO {

    @NotEmpty
    @Size(max = 30)
    private String firstName;

    @NotEmpty
    @Size(max = 40)
    private String lastName;

    @NotEmpty
    @Email
    private String email;

    private String activationCode;

    @Size(min = 8, max = 15)
    private String password;

    @NotNull
    private Integer resourceId;

    @Valid
    private CommentDTO comment;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public CommentDTO getComment() {
        return comment;
    }

    public void setComment(CommentDTO comment) {
        this.comment = comment;
    }

    public UserRegistrationDTO withFirstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserRegistrationDTO withLastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserRegistrationDTO withEmail(final String email) {
        this.email = email;
        return this;
    }

    public UserRegistrationDTO withActivationCode(final String activationCode) {
        this.activationCode = activationCode;
        return this;
    }

    public UserRegistrationDTO withPassword(final String password) {
        this.password = password;
        return this;
    }

    public UserRegistrationDTO withResourceId(final Integer resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public UserRegistrationDTO withComment(final CommentDTO comment) {
        this.comment = comment;
        return this;
    }

}
