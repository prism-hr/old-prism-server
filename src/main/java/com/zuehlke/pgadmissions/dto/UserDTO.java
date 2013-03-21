package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class UserDTO {

    private String firstName;

    private String lastName;
    
    private String email;
    
    private boolean newUser = true;

    private Program selectedProgram;
    
    private Authority[] selectedAuthorities;

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

    public Authority[] getSelectedAuthorities() {
        if (selectedAuthorities == null) {
            return new Authority[] {};
        }
        return selectedAuthorities;
    }

    public void setSelectedAuthorities(Authority... authorities) {
        this.selectedAuthorities = authorities;
    }

    public boolean isInAuthority(Authority authority) {
        for (Authority selectedAuthority : getSelectedAuthorities()) {
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
}
