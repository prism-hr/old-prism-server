package com.zuehlke.pgadmissions.rest.dto.auth;

import com.zuehlke.pgadmissions.domain.definitions.OauthProvider;

public class OauthUserDefinition {

    private OauthProvider oauthProvider;

    private String externalId;

    private String firstName;

    private String lastName;

    private String email;

    public OauthProvider getOauthProvider() {
        return oauthProvider;
    }

    public String getExternalId() {
        return externalId;
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

    public OauthUserDefinition withOauthProvider(final OauthProvider oauthProvider) {
        this.oauthProvider = oauthProvider;
        return this;
    }

    public OauthUserDefinition withExternalId(final String externalId) {
        this.externalId = externalId;
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


}
