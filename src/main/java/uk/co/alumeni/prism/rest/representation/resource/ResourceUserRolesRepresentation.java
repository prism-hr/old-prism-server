package uk.co.alumeni.prism.rest.representation.resource;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

import com.google.common.base.Objects;

public class ResourceUserRolesRepresentation {

    private UserRepresentationSimple user;

    private List<PrismRole> roles;

    private String message;

    private Boolean pending = false;

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

    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public ResourceUserRolesRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public ResourceUserRolesRepresentation withRoles(List<PrismRole> roles) {
        this.roles = roles;
        return this;
    }

    public ResourceUserRolesRepresentation withMessage(final String message) {
        this.message = message;
        return this;
    }

    public ResourceUserRolesRepresentation withPending(final Boolean pending) {
        this.pending = pending;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ResourceUserRolesRepresentation other = (ResourceUserRolesRepresentation) object;
        return Objects.equal(user, other.getUser());
    }

}
