package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.zuehlke.pgadmissions.rest.ActionDTO;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.dto.ProjectDTO;

public class UserRegistrationDTO {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    @Email
    private String email;

    private String activationCode;

    @NotEmpty
    @Size(min = 8, max = 15)
    private String password;

    @NotNull
    private Integer resourceId;

    private ActionDTO action;

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

    public ActionDTO getAction() {
        return action;
    }

    public void setAction(ActionDTO action) {
        this.action = action;
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

    public UserRegistrationDTO withAction(final ActionDTO action) {
        this.action = action;
        return this;
    }

}
