package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

public class ResourceUserRolesRepresentation {

    private UserRepresentation user;

    private List<PrismRole> roles;

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public List<PrismRole> getRoles() {
        return roles;
    }

    public void setRoles(List<PrismRole> roles) {
        this.roles = roles;
    }
    
    public ResourceUserRolesRepresentation withUser(UserRepresentation user)  {
        this.user = user;
        return this;
    }
    
    public ResourceUserRolesRepresentation withRoles(List<PrismRole> roles) {
        this.roles = roles;
        return this;
    }
    
}
