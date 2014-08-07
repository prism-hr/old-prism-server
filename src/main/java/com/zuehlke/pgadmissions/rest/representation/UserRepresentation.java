package com.zuehlke.pgadmissions.rest.representation;

public class UserRepresentation {

    private String firstName;

    private String lastName;

    private String email;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public UserRepresentation withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserRepresentation withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserRepresentation withEmail(String email) {
        this.email = email;
        return this;
    }

}
