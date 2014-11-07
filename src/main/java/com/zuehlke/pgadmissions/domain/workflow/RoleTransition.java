package com.zuehlke.pgadmissions.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;

@Entity
@Table(name = "ROLE_TRANSITION", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_transition_id", "role_id", "role_transition_type" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RoleTransition implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_transition_id", nullable = false)
    private StateTransition stateTransition;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "role_transition_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismRoleTransitionType roleTransitionType;

    @ManyToOne
    @JoinColumn(name = "transition_role_id", nullable = false)
    private Role transitionRole;

    @Column(name = "restrict_to_action_owner", nullable = false)
    private Boolean restrictToActionOwner;

    @Column(name = "minimum_permitted")
    private Integer minimumPermitted;

    @Column(name = "maximum_permitted")
    private Integer maximumPermitted;

    @ManyToOne
    @JoinColumn(name = "workflowPropertyDefinition")
    private WorkflowPropertyDefinition workflowPropertyDefinition;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StateTransition getStateTransition() {
        return stateTransition;
    }

    public void setStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public PrismRoleTransitionType getRoleTransitionType() {
        return roleTransitionType;
    }

    public void setRoleTransitionType(PrismRoleTransitionType roleTransitionType) {
        this.roleTransitionType = roleTransitionType;
    }

    public Boolean getRestrictToActionOwner() {
        return restrictToActionOwner;
    }

    public void setRestrictToActionOwner(Boolean restrictToActionOwner) {
        this.restrictToActionOwner = restrictToActionOwner;
    }

    public Role getTransitionRole() {
        return transitionRole;
    }

    public void setTransitionRole(Role transitionRole) {
        this.transitionRole = transitionRole;
    }

    public Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public void setMinimumPermitted(Integer minimumPermitted) {
        this.minimumPermitted = minimumPermitted;
    }

    public Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public void setMaximumPermitted(Integer maximumPermitted) {
        this.maximumPermitted = maximumPermitted;
    }

    public final WorkflowPropertyDefinition getWorkflowPropertyDefinition() {
        return workflowPropertyDefinition;
    }

    public final void setWorkflowPropertyDefinition(WorkflowPropertyDefinition workflowPropertyDefinition) {
        this.workflowPropertyDefinition = workflowPropertyDefinition;
    }

    public RoleTransition withStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
        return this;
    }

    public RoleTransition withRole(Role role) {
        this.role = role;
        return this;
    }

    public RoleTransition withRoleTransitionType(PrismRoleTransitionType roleTransitionType) {
        this.roleTransitionType = roleTransitionType;
        return this;
    }

    public RoleTransition withTransitionRole(Role transitionRole) {
        this.transitionRole = transitionRole;
        return this;
    }

    public RoleTransition withRestrictToActionOwner(boolean restrictToActionOwner) {
        this.restrictToActionOwner = restrictToActionOwner;
        return this;
    }

    public RoleTransition withMinimumPermitted(Integer minimumPermitted) {
        this.minimumPermitted = minimumPermitted;
        return this;
    }

    public RoleTransition withMaximumPermitted(Integer maximumPermitted) {
        this.maximumPermitted = maximumPermitted;
        return this;
    }
    
    public RoleTransition withWorkflowPropertyDefinition(WorkflowPropertyDefinition workflowPropertyDefinition) {
        this.workflowPropertyDefinition = workflowPropertyDefinition;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("stateTransition", stateTransition).addProperty("role", role)
                .addProperty("roleTransitionType", roleTransitionType);
    }

}
