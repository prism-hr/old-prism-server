package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

@Entity
@Table(name = "ROLE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Role extends WorkflowResource implements GrantedAuthority {

    private static final long serialVersionUID = 4265990408553249748L;

    @Id
    @Enumerated(EnumType.STRING)
    private PrismRole id;

    @Column(name = "is_scope_owner", nullable = false)
    private boolean scopeOwner;
    
    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ROLE_EXCLUSION", joinColumns = { @JoinColumn(name = "role_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "excluded_role_id", nullable = false) })
    private Set<Role> excludedRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = Sets.newHashSet();
    
    @OneToMany(mappedBy = "role")
    private Set<StateActionAssignment> stateActionAssignments = Sets.newHashSet();

    @Override
    public PrismRole getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = (PrismRole) id;
    }

    public boolean isScopeOwner() {
        return scopeOwner;
    }

    public void setScopeOwner(boolean scopeOwner) {
        this.scopeOwner = scopeOwner;
    }

    public Scope getScope() {
        return scope;
    }

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
    
    public Role withScopeOwner(boolean scopeOwner) {
        this.scopeOwner = scopeOwner;
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Role other = (Role) obj;
        return Objects.equal(id, other.getId());
    }

}
