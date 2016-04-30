package uk.co.alumeni.prism.domain.definitions.workflow;

import static com.google.common.base.Objects.equal;

import com.google.common.base.Objects;

public class PrismRoleTransition {

    private PrismRole role;

    private PrismRoleTransitionType transitionType;

    private PrismRole transitionRole;

    private Boolean restrictToActionOwner = false;

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    public PrismRole getRole() {
        return role;
    }

    public PrismRoleTransitionType getTransitionType() {
        return transitionType;
    }

    public PrismRole getTransitionRole() {
        return transitionRole;
    }

    public Boolean getRestrictToActionOwner() {
        return restrictToActionOwner;
    }

    public void setRestrictToActionOwner(Boolean restrictToActionOwner) {
        this.restrictToActionOwner = restrictToActionOwner;
    }

    public Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public PrismRoleTransition withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public PrismRoleTransition withTransitionType(PrismRoleTransitionType transitionType) {
        this.transitionType = transitionType;
        return this;
    }

    public PrismRoleTransition withTransitionRole(PrismRole transitionRole) {
        this.transitionRole = transitionRole;
        return this;
    }

    public PrismRoleTransition withRestrictToOwner() {
        this.restrictToActionOwner = true;
        return this;
    }

    public PrismRoleTransition withMinimumPermitted(Integer minimumPermitted) {
        this.minimumPermitted = minimumPermitted;
        return this;
    }

    public PrismRoleTransition withMaximumPermitted(Integer maximumPermitted) {
        this.maximumPermitted = maximumPermitted;
        return this;
    }

    public PrismRoleTransition withPropertyDefinition(PrismWorkflowConstraint propertyDefinition) {
        this.minimumPermitted = propertyDefinition.getMinimumPermitted();
        this.maximumPermitted = propertyDefinition.getMaximumPermitted();
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(role, transitionType, transitionRole);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        PrismRoleTransition other = (PrismRoleTransition) object;
        return equal(role, other.getRole()) && equal(transitionType, other.getTransitionType()) && equal(transitionRole, other.getTransitionRole())
                && equal(restrictToActionOwner, other.getRestrictToActionOwner()) && equal(minimumPermitted, other.getMinimumPermitted())
                && equal(maximumPermitted, other.getMaximumPermitted());
    }

}
