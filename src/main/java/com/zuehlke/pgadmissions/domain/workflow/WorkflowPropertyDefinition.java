package com.zuehlke.pgadmissions.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;

@Entity
@Table(name = "WORKFLOW_PROPERTY_DEFINITION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WorkflowPropertyDefinition extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismWorkflowPropertyDefinition id;

    @Column(name = "range_specification", nullable = false)
    private Boolean rangeSpecification;

    @Column(name = "minimum_permitted")
    private Integer minimumPermitted;

    @Column(name = "maximum_permitted")
    private Integer maximumPermitted;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public final PrismWorkflowPropertyDefinition getId() {
        return id;
    }

    public final void setId(PrismWorkflowPropertyDefinition id) {
        this.id = id;
    }

    public final Boolean getRangeSpecification() {
        return rangeSpecification;
    }

    public final void setRangeSpecification(Boolean rangeSpecification) {
        this.rangeSpecification = rangeSpecification;
    }

    public final Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public final void setMinimumPermitted(Integer minimumPermitted) {
        this.minimumPermitted = minimumPermitted;
    }

    public final Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public final void setMaximumPermitted(Integer maximumPermitted) {
        this.maximumPermitted = maximumPermitted;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public WorkflowPropertyDefinition withId(PrismWorkflowPropertyDefinition id) {
        this.id = id;
        return this;
    }

    public WorkflowPropertyDefinition withRangeSpecification(Boolean rangeSpecification) {
        this.rangeSpecification = rangeSpecification;
        return this;
    }

    public WorkflowPropertyDefinition withMinimumPermitted(Integer minimumPermitted) {
        this.minimumPermitted = minimumPermitted;
        return this;
    }

    public WorkflowPropertyDefinition withMaximumPermitted(Integer maximumPermitted) {
        this.maximumPermitted = maximumPermitted;
        return this;
    }

    public WorkflowPropertyDefinition withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
