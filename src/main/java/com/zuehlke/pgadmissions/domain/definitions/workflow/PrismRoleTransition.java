package com.zuehlke.pgadmissions.domain.definitions.workflow;

public class PrismRoleTransition {

    private PrismRole role;
    
    private PrismRoleTransitionType transitionType;
    
    private PrismRole transitionRole;
    
    private boolean restrictToActionOwner;
    
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

    public boolean isRestrictToActionOwner() {
        return restrictToActionOwner;
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
    
    public PrismRoleTransition withRestrictToActionOwner(boolean restrictToActionOwner) {
        this.restrictToActionOwner = restrictToActionOwner;
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
    
}
