package com.zuehlke.pgadmissions.rest.dto.user;

public class UserAccountDTO {

    private String email;

    private String password;

    public final String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        this.email = email;
    }

    public final String getPassword() {
        return password;
    }

    public final void setPassword(String password) {
        this.password = password;
    }

}
