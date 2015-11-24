package com.zuehlke.pgadmissions.rest.dto.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserAccountDTO {

    @NotNull
    private Boolean sendActivityNotification;

    @Size(min = 8, max = 15)
    private String password;

    public Boolean getSendActivityNotification() {
        return sendActivityNotification;
    }

    public void setSendActivityNotification(Boolean sendActivityNotification) {
        this.sendActivityNotification = sendActivityNotification;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
