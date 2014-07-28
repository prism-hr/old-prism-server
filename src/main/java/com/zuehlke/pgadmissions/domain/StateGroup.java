package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

@Entity
@Table(name = "STATE_GROUP", uniqueConstraints = { @UniqueConstraint(columnNames = { "scope_id", "sequence_order" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StateGroup extends WorkflowResource {
    
    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismStateGroup id;
    
    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder;
    
    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public PrismStateGroup getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = (PrismStateGroup) id;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
    
    public StateGroup withId(PrismStateGroup id) {
        this.id = id;
        return this;
    }
    
    public StateGroup withSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
        return this;
    }
    
    public StateGroup withScope(Scope scope) {
        this.scope = scope;
        return this;
    }
    
}
