package com.zuehlke.pgadmissions.rest.representation;

public class UserExtendedRepresentation {

    private String firstName;

    private String firstName2;

    private String firstName3;

    private String lastName;

    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName2() {
        return firstName2;
    }

    public void setFirstName2(String firstName2) {
        this.firstName2 = firstName2;
    }

    public String getFirstName3() {
        return firstName3;
    }

    public void setFirstName3(String firstName3) {
        this.firstName3 = firstName3;
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
    
    public UserExtendedRepresentation withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public UserExtendedRepresentation withFirstName2(String firstName2) {
        this.firstName2 = firstName2;
        return this;
    }
    
    public UserExtendedRepresentation withFirstName3(String firstName3) {
        this.firstName3 = firstName3;
        return this;
    }
    
    public UserExtendedRepresentation withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public UserExtendedRepresentation withEmail(String email) {
        this.email = email;
        return this;
    }
    
}
