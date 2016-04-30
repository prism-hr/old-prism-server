package uk.co.alumeni.prism.rest.dto.user;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import uk.co.alumeni.prism.rest.UserDescriptor;

public class UserDTO extends UserDescriptor {

    private Integer id;

    @NotEmpty
    @Size(max = 30)
    private String firstName;

    @NotEmpty
    @Size(max = 40)
    private String lastName;

    @Email
    @NotEmpty
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public UserDTO withId(Integer id) {
        this.id = id;
        return this;
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
