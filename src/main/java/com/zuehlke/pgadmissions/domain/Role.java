package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Entity
@Table(name = "ROLE")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Role implements GrantedAuthority {

    private static final long serialVersionUID = 4265990408553249748L;

    @Id
    @Enumerated(EnumType.STRING)
    private Authority id;
    
    @OneToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles;

    public Authority getId() {
        return id;
    }

    public void setId(Authority id) {
        this.id = id;
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

    public Role withId(Authority id) {
        this.id = id;
        return this;
    }

}
