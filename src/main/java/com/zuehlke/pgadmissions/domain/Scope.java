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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;

@Entity
@Table(name = "SCOPE")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Scope {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismScope id;
    
    @Column(name = "precedence", nullable = false, unique = true)
    private Integer precedence;
    
    @ManyToMany
    @JoinTable(name = "SCOPE_CREATION", joinColumns = { @JoinColumn(name = "scope_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "created_scope_id", nullable = false) })
    private Set<Scope> scopeCreations = Sets.newHashSet();
    
    @OneToMany(mappedBy = "scope")
    private Set<State> states = Sets.newHashSet();

    public Scope() {
    }
    
    public Scope(PrismScope id, Integer precedence) {
        this.id = id;
        this.precedence = precedence;
    }
    
    public PrismScope getId() {
        return id;
    }

    public void setId(PrismScope id) {
        this.id = id;
    }
    
    public Integer getPrecedence() {
        return precedence;
    }

    public void setPrecedence(Integer precedence) {
        this.precedence = precedence;
    }
    
    public Set<State> getStates() {
        return states;
    }

    public Set<Scope> getScopeCreations() {
        return scopeCreations;
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
        final Scope otherScope = (Scope) object;
        return Objects.equal(id, otherScope.getId());
    }
    
}
