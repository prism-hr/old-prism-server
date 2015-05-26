package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.base.Objects;

public class PrismRoleTransition {

    private PrismRole role;

    private PrismRoleTransitionType transitionType;
    
    private Boolean partnerMode = false;

    private PrismRole transitionRole;

    private Boolean restrictToActionOwner = false;

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    private PrismWorkflowPropertyDefinition propertyDefinition;

    public PrismRole getRole() {
        return role;
    }

    public PrismRoleTransitionType getTransitionType() {
        return transitionType;
    }
    
    public Boolean getPartnerMode() {
        return partnerMode;
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

    public final PrismWorkflowPropertyDefinition getPropertyDefinition() {
        return propertyDefinition;
    }

    public PrismRoleTransition withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public PrismRoleTransition withTransitionType(PrismRoleTransitionType transitionType) {
        this.transitionType = transitionType;
        return this;
    }

    public PrismRoleTransition withPartnerMode() {
        this.partnerMode = true;
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
    
    public PrismRoleTransition withPropertyDefinition(PrismWorkflowPropertyDefinition propertyDefinition) {
        this.propertyDefinition = propertyDefinition;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(role, transitionType, transitionRole, restrictToActionOwner, minimumPermitted, maximumPermitted, propertyDefinition);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PrismRoleTransition other = (PrismRoleTransition) obj;
        return Objects.equal(role, other.getRole()) && Objects.equal(transitionType, other.getTransitionType())
                && Objects.equal(transitionRole, other.getTransitionRole()) && Objects.equal(restrictToActionOwner, other.getRestrictToActionOwner())
                && Objects.equal(minimumPermitted, other.getMinimumPermitted()) && Objects.equal(maximumPermitted, other.getMaximumPermitted())
                && Objects.equal(propertyDefinition, other.getPropertyDefinition());
    }

}
