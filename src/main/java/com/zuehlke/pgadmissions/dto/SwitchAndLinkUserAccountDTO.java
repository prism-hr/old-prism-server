package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class SwitchAndLinkUserAccountDTO {

    @ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
    private String email;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String currentPassword;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String password;

    public SwitchAndLinkUserAccountDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    } 
    
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}
