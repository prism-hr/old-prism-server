package uk.co.alumeni.prism.rest.representation.resource;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang3.ObjectUtils.compare;

import java.util.Set;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

import com.google.common.base.Objects;

public class ResourceUserRolesRepresentation implements Comparable<ResourceUserRolesRepresentation> {

    private UserRepresentationSimple user;

    private Set<PrismRole> roles;

    private String message;

    private boolean owner;

    private boolean pending;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public Set<PrismRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<PrismRole> roles) {
        this.roles = roles;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public ResourceUserRolesRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public ResourceUserRolesRepresentation withRoles(Set<PrismRole> roles) {
        this.roles = roles;
        return this;
    }

    public ResourceUserRolesRepresentation withMessage(String message) {
        this.message = message;
        return this;
    }

    public ResourceUserRolesRepresentation withOwner(boolean owner) {
        this.owner = owner;
        return this;
    }

    public ResourceUserRolesRepresentation withPending(boolean pending) {
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
        final ResourceUserRolesRepresentation other = (ResourceUserRolesRepresentation) object;
        return equal(user, other.getUser());
    }

    @Override
    public int compareTo(ResourceUserRolesRepresentation other) {
        int compare = compare(owner, other.isOwner());
        return compare == 0 ? compare(user, other.getUser()) : compare;
    }

}
