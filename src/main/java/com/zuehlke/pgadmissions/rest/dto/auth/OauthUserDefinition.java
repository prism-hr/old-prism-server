package com.zuehlke.pgadmissions.rest.dto.auth;

public class OauthUserDefinition {

    private String linkedinId;

    private String firstName;

    private String lastName;

    private String email;

    private String accountProfileUrl;

    private String accountImageUrl;

    public String getLinkedinId() {
        return linkedinId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountProfileUrl() {
        return accountProfileUrl;
    }

    public String getAccountImageUrl() {
        return accountImageUrl;
    }

    public OauthUserDefinition withLinkedinId(final String linkedinId) {
        this.linkedinId = linkedinId;
        return this;
    }

    public OauthUserDefinition withFirstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public OauthUserDefinition withLastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public OauthUserDefinition withEmail(final String email) {
        this.email = email;
        return this;
    }

    public OauthUserDefinition withAccountProfileUrl(final String accountProfileUrl) {
        this.accountProfileUrl = accountProfileUrl;
        return this;
    }

    public OauthUserDefinition withAccountImageUrl(final String accountImageUrl) {
        this.accountImageUrl = accountImageUrl;
        return this;
    }

}
