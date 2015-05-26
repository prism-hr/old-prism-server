package com.zuehlke.pgadmissions.rest.dto.user;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class UserLinkingDTO {

    @NotEmpty
    private String currentPassword;

    @Email
    @NotEmpty
    private String otherEmail;

    @NotEmpty
    private String otherPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getOtherEmail() {
        return otherEmail;
    }

    public void setOtherEmail(String otherEmail) {
        this.otherEmail = otherEmail;
    }

    public String getOtherPassword() {
        return otherPassword;
    }

    public void setOtherPassword(String otherPassword) {
        this.otherPassword = otherPassword;
    }
}
