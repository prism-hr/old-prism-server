package com.zuehlke.pgadmissions.security;

public class TokenValidityStatus {

    private boolean valid;

    private String renewedToken;

    public TokenValidityStatus(boolean valid, String renewedToken) {
        this.valid = valid;
        this.renewedToken = renewedToken;
    }

    public boolean isValid() {
        return valid;
    }

    public String getRenewedToken() {
        return renewedToken;
    }
}
