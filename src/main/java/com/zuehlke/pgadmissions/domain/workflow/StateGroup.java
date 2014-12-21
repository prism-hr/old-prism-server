package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

import javax.persistence.*;

@Entity
@Table(name = "STATE_GROUP", uniqueConstraints = {@UniqueConstraint(columnNames = {"scope_id", "sequence_order"})})
public class StateGroup extends WorkflowDefinition {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismStateGroup id;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @Column(name = "repeatable", nullable = false)
    private Boolean repeatable;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public PrismStateGroup getId() {
        return id;
    }

    public void setId(PrismStateGroup id) {
        this.id = id;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public final Boolean getRepeatable() {
        return repeatable;
    }

    public final void setRepeatable(Boolean repeatable) {
        this.repeatable = repeatable;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
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

    public StateGroup withRepeatable(Boolean repeatable) {
        this.repeatable = repeatable;
        return this;
    }

    public StateGroup withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
