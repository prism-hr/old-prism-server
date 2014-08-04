package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    @NotNull
    private PrismAction actionId;
    
    @Valid
    private InstitutionDTO newInstitution;
    
    @Valid
    private ProgramDTO newProgram;
    
    @Valid
    private ProjectDTO newProject;
    
    @Valid
    private ApplicationDTO newApplication;

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

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
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

    public final ProjectDTO getNewProject() {
        return newProject;
    }

    public final void setNewProject(ProjectDTO newProject) {
        this.newProject = newProject;
    }

    public final ApplicationDTO getNewApplication() {
        return newApplication;
    }

    public final void setNewApplication(ApplicationDTO newApplication) {
        this.newApplication = newApplication;
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
    
    public UserRegistrationDTO withAction(final PrismAction actionId) {
        this.actionId = actionId;
        return this;
    }
    
    public UserRegistrationDTO withNewInstitution(InstitutionDTO newInstitution) {
        this.newInstitution = newInstitution;
        return this;
    }
    
    public UserRegistrationDTO withNewProgram(ProgramDTO newProgram) {
        this.newProgram = newProgram;
        return this;
    }
    
    public UserRegistrationDTO withNewProject(ProjectDTO newProject) {
        this.newProject = newProject;
        return this;
    }
    
    public UserRegistrationDTO withNewApplication(ApplicationDTO newApplication) {
        this.newApplication = newApplication;
        return this;
    }
    
}
