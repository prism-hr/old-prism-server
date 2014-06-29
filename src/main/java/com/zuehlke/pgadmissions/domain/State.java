package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_state_id")
    private State parentState;
    
    @Column(name = "sequence_order")
    private Integer sequenceOrder;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;
    
    @OneToMany(mappedBy = "state")
    private Set<StateAction> stateActions = Sets.newHashSet();
    
    @OneToMany(mappedBy = "transitionState")
    private Set<StateTransition> inverseStateTransitions = Sets.newHashSet();

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

    public Set<StateTransition> getInverseStateTransitions() {
        return inverseStateTransitions;
    }

    public State withId(PrismState id) {
        this.id = id;
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
