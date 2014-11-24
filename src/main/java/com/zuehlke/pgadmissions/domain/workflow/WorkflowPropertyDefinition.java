package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "WORKFLOW_PROPERTY_DEFINITION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WorkflowPropertyDefinition extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismWorkflowPropertyDefinition id;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismWorkflowPropertyCategory category;

    @Column(name = "define_range", nullable = false)
    private Boolean defineRange;

    @Column(name = "can_be_disabled", nullable = false)
    private Boolean canBeDisabled;

    @Column(name = "minimum_permitted")
    private Integer minimumPermitted;

    @Column(name = "maximum_permitted")
    private Integer maximumPermitted;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public PrismWorkflowPropertyDefinition getId() {
        return id;
    }

    public void setId(PrismWorkflowPropertyDefinition id) {
        this.id = id;
    }

    public PrismWorkflowPropertyCategory getCategory() {
        return category;
    }

    public void setCategory(PrismWorkflowPropertyCategory category) {
        this.category = category;
    }

    public Boolean getDefineRange() {
        return defineRange;
    }

    public void setDefineRange(Boolean defineRange) {
        this.defineRange = defineRange;
    }

    public Boolean getCanBeDisabled() {
        return canBeDisabled;
    }

    public void setCanBeDisabled(Boolean canBeDisabled) {
        this.canBeDisabled = canBeDisabled;
    }

    public Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public void setMinimumPermitted(Integer minimumPermitted) {
        this.minimumPermitted = minimumPermitted;
    }

    public Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public void setMaximumPermitted(Integer maximumPermitted) {
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

    public WorkflowPropertyDefinition withCategory(PrismWorkflowPropertyCategory category) {
        this.category = category;
        return this;
    }

    public WorkflowPropertyDefinition withDefineRange(Boolean defineRange) {
        this.defineRange = defineRange;
        return this;
    }

    public WorkflowPropertyDefinition withCanBeDisabled(Boolean canBeDisabled) {
        this.canBeDisabled = canBeDisabled;
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

    public Boolean getDefineOnOrOff() {
        return !defineRange;
    }

}
