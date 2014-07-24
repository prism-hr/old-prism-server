package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class RegistrationDetails {

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

    @NotNull
    private PrismAction registrationAction;

    private InstitutionDTO newInstitution;

    private ProgramDTO newProgram;

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

    public PrismAction getRegistrationAction() {
        return registrationAction;
    }

    public void setRegistrationAction(PrismAction registrationAction) {
        this.registrationAction = registrationAction;
    }

    public InstitutionDTO getNewInstitution() {
        return newInstitution;
    }

    public void setNewInstitution(InstitutionDTO newInstitution) {
        this.newInstitution = newInstitution;
    }

    public ProgramDTO getNewProgram() {
        return newProgram;
    }

    public void setNewProgram(ProgramDTO newProgram) {
        this.newProgram = newProgram;
    }

    public RegistrationDetails withFirstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public RegistrationDetails withLastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public RegistrationDetails withEmail(final String email) {
        this.email = email;
        return this;
    }

    public RegistrationDetails withActivationCode(final String activationCode) {
        this.activationCode = activationCode;
        return this;
    }

    public RegistrationDetails withPassword(final String password) {
        this.password = password;
        return this;
    }

    public RegistrationDetails withResourceId(final Integer resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public RegistrationDetails withAction(final PrismAction actionId) {
        this.registrationAction = actionId;
        return this;
    }


}
