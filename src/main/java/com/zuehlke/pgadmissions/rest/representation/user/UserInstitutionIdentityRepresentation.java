package com.zuehlke.pgadmissions.rest.representation.user;

import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;

public class UserInstitutionIdentityRepresentation {

    private PrismUserIdentity identityType;

    private String identifier;

    public PrismUserIdentity getIdentityType() {
        return identityType;
    }

    public void setIdentityType(PrismUserIdentity identityType) {
        this.identityType = identityType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
