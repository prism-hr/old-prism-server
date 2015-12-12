package uk.co.alumeni.prism.dto;

import com.google.common.base.Objects;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

public class ResourceRoleDTO {

    private PrismScope scope;

    private Integer id;

    private PrismRole role;

    private Boolean verified;

    private Boolean directlyAssignable;

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PrismRole getRole() {
        return role;
    }

    public void setRole(PrismRole role) {
        this.role = role;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getDirectlyAssignable() {
        return directlyAssignable;
    }

    public void setDirectlyAssignable(Boolean directlyAssignable) {
        this.directlyAssignable = directlyAssignable;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, role);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ResourceRoleDTO other = (ResourceRoleDTO) object;
        return Objects.equal(scope, other.getScope()) && Objects.equal(id, other.getId()) && Objects.equal(role, other.getRole());
    }

}
