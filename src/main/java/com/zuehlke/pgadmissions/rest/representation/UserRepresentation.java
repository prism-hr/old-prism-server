package com.zuehlke.pgadmissions.rest.representation;

public class UserRepresentation {

    private Integer id;

    private String firstName;

    private String lastName;

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
