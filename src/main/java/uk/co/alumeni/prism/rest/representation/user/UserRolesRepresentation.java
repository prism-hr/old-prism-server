package uk.co.alumeni.prism.rest.representation.user;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationIdentity;

public class UserRolesRepresentation {

    private ResourceRepresentationIdentity resource;

    private List<PrismRole> roles;

    public UserRolesRepresentation(ResourceRepresentationIdentity resource, List<PrismRole> roles) {
        this.resource = resource;
        this.roles = roles;
    }

    public ResourceRepresentationIdentity getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationIdentity resource) {
        this.resource = resource;
    }

    public List<PrismRole> getRoles() {
        return roles;
    }

    public void setRoles(List<PrismRole> roles) {
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
