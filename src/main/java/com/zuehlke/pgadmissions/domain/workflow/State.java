package com.zuehlke.pgadmissions.domain.workflow;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "STATE")
public class State extends WorkflowDefinition {

    @Id
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

    @Column(name = "parallelizable")
    private Boolean parallelizable;

    @Column(name = "hidden")
    private Boolean hidden;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @OneToMany(mappedBy = "state")
    private Set<StateAction> stateActions = Sets.newHashSet();

    @OneToMany(mappedBy = "state")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @OneToMany(mappedBy = "state")
    private Set<ResourcePreviousState> resourcePreviousStates = Sets.newHashSet();

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

    public StateDurationDefinition getStateDurationDefinition() {
        return stateDurationDefinition;
    }

    public void setStateDurationDefinition(StateDurationDefinition stateDurationDefinition) {
        this.stateDurationDefinition = stateDurationDefinition;
    }

    public PrismStateDurationEvaluation getStateDurationEvaluation() {
        return stateDurationEvaluation;
    }

    public void setStateDurationEvaluation(PrismStateDurationEvaluation stateDurationEvaluation) {
        this.stateDurationEvaluation = stateDurationEvaluation;
    }

    public Boolean getParallelizable() {
        return parallelizable;
    }

    public void setParallelizable(Boolean parallelizable) {
        this.parallelizable = parallelizable;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Set<StateAction> getStateActions() {
        return stateActions;
    }

    public Set<ResourceState> getResourceStates() {
        return resourceStates;
    }

    public Set<ResourcePreviousState> getResourcePreviousStates() {
        return resourcePreviousStates;
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

    public State withHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public State withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
