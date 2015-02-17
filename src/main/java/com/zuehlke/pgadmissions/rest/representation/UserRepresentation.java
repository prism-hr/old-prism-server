package com.zuehlke.pgadmissions.rest.representation;

public class UserRepresentation {

    private Integer id;

    private String firstName;

    private String firstName2;

    private String firstName3;

    private String lastName;

    private String email;

    private String accountProfileUrl;

    private String accountImageUrl;

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

    public String getAccountProfileUrl() {
        return accountProfileUrl;
    }

    public void setAccountProfileUrl(String accountProfileUrl) {
        this.accountProfileUrl = accountProfileUrl;
    }

    public String getAccountImageUrl() {
        return accountImageUrl;
    }

    public void setAccountImageUrl(String accountImageUrl) {
        this.accountImageUrl = accountImageUrl;
    }

    public UserRepresentation withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserRepresentation withFirstName2(String firstName2) {
        this.firstName2 = firstName2;
        return this;
    }

    public UserRepresentation withFirstName3(String firstName3) {
        this.firstName3 = firstName3;
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

    public UserRepresentation withAccountImageUrl(final String accountImageUrl) {
        this.accountImageUrl = accountImageUrl;
        return this;
    }

    public UserRepresentation withAccountProfileUrl(final String accountProfileUrl) {
        this.accountProfileUrl = accountProfileUrl;
        return this;
    }

}
