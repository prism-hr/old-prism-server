package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.Set;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

public class ResourceUserRolesRepresentation {

    private UserRepresentation user;

    private Set<PrismRole> roles;

    public ResourceUserRolesRepresentation(UserRepresentation user, Set<PrismRole> roles) {
        this.user = user;
        this.roles = roles;
    }

    public ResourceUserRolesRepresentation() {
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public Set<PrismRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<PrismRole> roles) {
        this.roles = roles;
    }
}
