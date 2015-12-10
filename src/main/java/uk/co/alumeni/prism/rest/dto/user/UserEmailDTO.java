package uk.co.alumeni.prism.rest.dto.user;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class UserEmailDTO {

    @NotEmpty
    @Email
    private String email;

    public final String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        this.email = email;
    }

}
