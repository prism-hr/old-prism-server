package com.zuehlke.pgadmissions.rest.dto.auth;

import javax.validation.constraints.NotNull;

public class OauthLoginDTO {

    private String clientId;

    private String redirectUri;

    private String code;

    @NotNull
    private OauthAssociationType associationType;

    /**
     * e.g. used to identify user when he's invited
     */
    private String activationCode;

    // OAUTH 1.0 fields
    private String oauthToken;

    private String oauthVerifier;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OauthAssociationType getAssociationType() {
        return associationType;
    }

    public void setAssociationType(OauthAssociationType associationType) {
        this.associationType = associationType;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getOauthVerifier() {
        return oauthVerifier;
    }

    public void setOauthVerifier(String oauthVerifier) {
        this.oauthVerifier = oauthVerifier;
    }
}
