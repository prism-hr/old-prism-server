package com.zuehlke.pgadmissions.rest.representation.user;

import com.zuehlke.pgadmissions.domain.definitions.PrismUserInstitutionIdentity;

public class UserInstitutionIdentityRepresentation {

    private PrismUserInstitutionIdentity identityType;

    private String identifier;

    public PrismUserInstitutionIdentity getIdentityType() {
        return identityType;
    }

    public void setIdentityType(PrismUserInstitutionIdentity identityType) {
        this.identityType = identityType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public UserInstitutionIdentityRepresentation withIdentityType(PrismUserInstitutionIdentity identityType) {
        this.identityType = identityType;
        return this;
    }
    
    public UserInstitutionIdentityRepresentation withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

}
