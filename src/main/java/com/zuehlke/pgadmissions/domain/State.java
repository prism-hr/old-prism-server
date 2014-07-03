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

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@Entity
@Table(name = "STATE")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class State extends WorkflowResource {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismState id;

    @ManyToOne
    @JoinColumn(name = "parent_state_id")
    private State parentState;
    
    @Column(name = "is_initial_state", nullable = false)
    private boolean initialState;
    
    @Column(name = "is_final_state", nullable = false)
    private boolean finalState;

    @Column(name = "sequence_order")
    private Integer sequenceOrder;
    
    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;
    
    @OneToMany(mappedBy = "state")
    private Set<StateAction> stateActions = Sets.newHashSet();

    @Override
    public PrismState getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = (PrismState) id;
    }
    
    public State getParentState() {
        return parentState;
    }

    public void setParentState(State parentState) {
        this.parentState = parentState;
    }
    
    public boolean isInitialState() {
        return initialState;
    }

    public void setInitialState(boolean initialState) {
        this.initialState = initialState;
    }

    public boolean isFinalState() {
        return finalState;
    }

    public void setFinalState(boolean finalState) {
        this.finalState = finalState;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
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
    
    public State withInitialState(boolean initialState) {
        this.initialState = initialState;
        return this;
    }
    
    public State withFinalState(boolean finalState) {
        this.finalState = finalState;
        return this;
    }
    
    public State withSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
        return this;
    }
    
    public State withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
