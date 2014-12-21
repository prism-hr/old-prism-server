package com.zuehlke.pgadmissions.domain.workflow;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "ROLE")
public class Role extends WorkflowDefinition implements GrantedAuthority {

    private static final long serialVersionUID = 4265990408553249748L;

    @Id
    @Enumerated(EnumType.STRING)
    private PrismRole id;

    @Column(name = "scope_creator", nullable = false)
    private Boolean scopeCreator;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ROLE_EXCLUSION", joinColumns = {@JoinColumn(name = "role_id", nullable = false)}, inverseJoinColumns = {@JoinColumn(name = "excluded_role_id", nullable = false)})
    private Set<Role> excludedRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "role")
    private Set<StateActionAssignment> stateActionAssignments = Sets.newHashSet();

    @Override
    public PrismRole getId() {
        return id;
    }

    public void setId(PrismRole id) {
        this.id = id;
    }

    public Boolean getScopeCreator() {
        return scopeCreator;
    }

    public void setScopeCreator(Boolean scopeCreator) {
        this.scopeCreator = scopeCreator;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public Set<Role> getExcludedRoles() {
        return excludedRoles;
    }

    public Set<StateActionAssignment> getStateActionAssignments() {
        return stateActionAssignments;
    }

    public Role withId(PrismRole id) {
        this.id = id;
        return this;
    }

    public Role withScopeCreator(Boolean scopeCreator) {
        this.scopeCreator = scopeCreator;
        return this;
    }

    public Role withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public String getAuthority() {
        return id.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Role other = (Role) object;
        return Objects.equal(id, other.getId());
    }

}
