package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.enums.RoleTransitionType;

@Entity
@Table(name = "ROLE_TRANSITION")
public class RoleTransition {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_transition_id", nullable = false)
    private StateTransition stateTransition;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "role_transition_type_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleTransitionType type;

    @Column(name = "processing_order")
    private Integer processingOrder;

    @ManyToOne
    @JoinColumn(name = "transition_role_id")
    private Role transitionRole;

    @Column(name = "minimum_permitted")
    private Integer minimumPermitted;

    @Column(name = "maximum_permitted")
    private Integer maximumPermitted;

    @Column(name = "restrict_to_invoker")
    private Boolean restrictToInvoker;

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

    public RoleTransitionType getType() {
        return type;
    }

    public void setType(RoleTransitionType type) {
        this.type = type;
    }

    public Integer getProcessingOrder() {
        return processingOrder;
    }

    public void setProcessingOrder(Integer processingOrder) {
        this.processingOrder = processingOrder;
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

    public Boolean getRestrictToInvoker() {
        return restrictToInvoker;
    }

    public void setRestrictToInvoker(Boolean restrictToInvoker) {
        this.restrictToInvoker = restrictToInvoker;
    }

}
