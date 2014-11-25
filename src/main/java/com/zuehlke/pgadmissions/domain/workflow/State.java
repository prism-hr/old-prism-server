package com.zuehlke.pgadmissions.domain.workflow;

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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;

@Entity
@Table(name = "STATE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class State extends WorkflowDefinition {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismState id;

    @ManyToOne
    @JoinColumn(name = "state_group_id", nullable = false)
    private StateGroup stateGroup;

    @ManyToOne
    @JoinColumn(name = "state_duration_definition_id")
    private StateDurationDefinition stateDurationDefinition;
    
    @Column(name = "state_duration_evaluation")
    @Enumerated(EnumType.STRING)
    private PrismStateDurationEvaluation stateDurationEvaluation;

    @Column(name = "parallelizable", nullable = false)
    private Boolean parallelizable;
    
    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;
    
    @OneToMany(mappedBy = "state")
    private Set<StateAction> stateActions = Sets.newHashSet();
    
    @OneToMany(mappedBy = "state")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @Override
    public PrismState getId() {
        return id;
    }

    public void setId(PrismState id) {
        this.id = id;
    }

    public StateGroup getStateGroup() {
        return stateGroup;
    }

    public void setStateGroup(StateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }

    public final StateDurationDefinition getStateDurationDefinition() {
        return stateDurationDefinition;
    }

    public final void setStateDurationDefinition(StateDurationDefinition stateDurationDefinition) {
        this.stateDurationDefinition = stateDurationDefinition;
    }

    public final PrismStateDurationEvaluation getStateDurationEvaluation() {
        return stateDurationEvaluation;
    }

    public final void setStateDurationEvaluation(PrismStateDurationEvaluation stateDurationEvaluation) {
        this.stateDurationEvaluation = stateDurationEvaluation;
    }

    public final Boolean getParallelizable() {
        return parallelizable;
    }

    public final void setParallelizable(Boolean parallelizable) {
        this.parallelizable = parallelizable;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }
    
    public final Set<StateAction> getStateActions() {
        return stateActions;
    }

    public final Set<ResourceState> getResourceStates() {
        return resourceStates;
    }

    public State withId(PrismState id) {
        this.id = id;
        return this;
    }

    public State withStateGroup(StateGroup stateGroup) {
        this.stateGroup = stateGroup;
        return this;
    }

    public State withStateDurationDefinition(StateDurationDefinition stateDurationDefinition) {
        this.stateDurationDefinition = stateDurationDefinition;
        return this;
    }
    
    public State withStateDurationEvaluation(PrismStateDurationEvaluation stateDurationEvaluation) {
        this.stateDurationEvaluation = stateDurationEvaluation;
        return this;
    }
    
    public State withParallelizable(Boolean parallelizable) {
        this.parallelizable = parallelizable;
        return this;
    }

    public State withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
