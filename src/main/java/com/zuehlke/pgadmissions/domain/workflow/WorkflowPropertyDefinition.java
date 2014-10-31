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

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory;

@Entity
@Table(name = "WORKFLOW_PROPERTY_DEFINITION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WorkflowPropertyDefinition extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismDisplayProperty id;

    @Column(name = "workflow_property_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismWorkflowPropertyCategory workflowPropertyCategory;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public PrismDisplayProperty getId() {
        return id;
    }

    public void setId(PrismDisplayProperty id) {
        this.id = id;
    }

    public final PrismWorkflowPropertyCategory getWorkflowPropertyCategory() {
        return workflowPropertyCategory;
    }

    public final void setWorkflowPropertyCategory(PrismWorkflowPropertyCategory workflowPropertyCategory) {
        this.workflowPropertyCategory = workflowPropertyCategory;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public WorkflowPropertyDefinition withId(PrismDisplayProperty id) {
        this.id = id;
        return this;
    }
    
    public WorkflowPropertyDefinition withWorkflowPropertyCategory(PrismWorkflowPropertyCategory workflowPropertyCategory) {
        this.workflowPropertyCategory = workflowPropertyCategory;
        return this;
    }

    public WorkflowPropertyDefinition withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
