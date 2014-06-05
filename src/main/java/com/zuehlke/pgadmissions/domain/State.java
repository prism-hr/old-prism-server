package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Entity
@Table(name = "STATE")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class State {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismState id;

    @ManyToOne
    @JoinColumn(name = "parent_state_id", nullable = false)
    private State parentState;
    
    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;
    
    @OneToMany(mappedBy = "state")
    private Set<StateAction> stateActions;
    
    public PrismState getId() {
        return id;
    }

    public void setId(PrismState id) {
        this.id = id;
    }

    public State getParentState() {
        return parentState;
    }

    public void setParentState(State parentState) {
        this.parentState = parentState;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Set<StateAction> getStateActions() {
        return stateActions;
    }
    
    public State withId(PrismState id) {
        this.id = id;
        return this;
    }

}
