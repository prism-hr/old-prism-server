package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ResourceUserRolesRepresentation {

    private UserRepresentationSimple user;

    private List<PrismRole> roles;
    
    private String message;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public List<PrismRole> getRoles() {
        return roles;
    }

    public void setRoles(List<PrismRole> roles) {
        this.roles = roles;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResourceUserRolesRepresentation withUser(UserRepresentationSimple user)  {
        this.user = user;
        return this;
    }
    
    public ResourceUserRolesRepresentation withRoles(List<PrismRole> roles) {
        this.roles = roles;
        return this;
    }
    
}
