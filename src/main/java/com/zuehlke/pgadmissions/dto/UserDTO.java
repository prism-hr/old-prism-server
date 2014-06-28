package com.zuehlke.pgadmissions.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

public class UserDTO {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
    
    @NotBlank
    @Email
    private String email;
    
    private boolean newUser = true;

    private Program selectedProgram;
    
    private PrismRole[] selectedAuthorities;

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

    public Program getSelectedProgram() {
        return selectedProgram;
    }

    public void setSelectedProgram(Program selectedprogram) {
        this.selectedProgram = selectedprogram;
    }

    public PrismRole[] getSelectedAuthorities() {
        if (selectedAuthorities == null) {
            return new PrismRole[] {};
        }
        return selectedAuthorities;
    }

    public void setSelectedAuthorities(PrismRole... authorities) {
        this.selectedAuthorities = authorities;
    }

    public boolean isInAuthority(PrismRole authority) {
        for (PrismRole selectedAuthority : getSelectedAuthorities()) {
            if (selectedAuthority == authority) {
                return true;
            }
        }
        return false;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }
    
    public UserDTO withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public UserDTO withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public UserDTO withEmail(String email) {
        this.email = email;
        return this;
    }
}
