package com.zuehlke.pgadmissions.rest.representation.user;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationIdentity;
import com.zuehlke.pgadmissions.rest.representation.workflow.RoleRepresentation;

public class UserRolesRepresentation {

    private ResourceRepresentationIdentity resource;

    private List<RoleRepresentation> roles;

    public UserRolesRepresentation(ResourceRepresentationIdentity resource, List<RoleRepresentation> roles) {
        this.resource = resource;
        this.roles = roles;
    }

    public ResourceRepresentationIdentity getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationIdentity resource) {
        this.resource = resource;
    }

    public List<RoleRepresentation> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleRepresentation> roles) {
        this.roles = roles;
    }

    @Override
    public int hashCode() {
        return resource.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return resource.equals(object);
    }

}
