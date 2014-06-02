package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.enums.RoleTransitionType;

@Entity
@Table(name = "ROLE_TRANSITION", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_transition_id", "role_id", "role_transition_type_id",
        "restrict_to_action_owner", "transition_role_id" }) })
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
    private RoleTransitionType roleTransitionType;

    @Column(name = "restrict_to_action_owner", nullable = false)
    private boolean restrictToActionOwner;

    @ManyToOne
    @JoinColumn(name = "transition_role_id", nullable = false)
    private Role transitionRole;

    @Column(name = "minimum_permitted")
    private Integer minimumPermitted;

    @Column(name = "maximum_permitted")
    private Integer maximumPermitted;

    @ManyToMany
    @JoinTable(name = "ROLE_TRANSITION_EXCLUSION", joinColumns = { @JoinColumn(name = "role_transition_id", nullable = false) }, //
            inverseJoinColumns = { @JoinColumn(name = "role_id", nullable = false) }, //
            uniqueConstraints = { @UniqueConstraint(columnNames = { "role_transition_id", "role_id" }) })
    private Set<Role> excludedRoles = Sets.newHashSet();

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

    public RoleTransitionType getRoleTransitionType() {
        return roleTransitionType;
    }

    public void setRoleTransitionType(RoleTransitionType roleTransitionType) {
        this.roleTransitionType = roleTransitionType;
    }

    public boolean isRestrictToActionOwner() {
        return restrictToActionOwner;
    }

    public void setRestrictToActionOwner(boolean restrictToActionOwner) {
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

    public Set<Role> getExcludedRoles() {
        return excludedRoles;
    }

}
