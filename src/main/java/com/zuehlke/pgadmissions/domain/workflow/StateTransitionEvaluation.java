package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;

import javax.persistence.*;

@Entity
@Table(name = "state_transition_evaluation")
public class StateTransitionEvaluation extends WorkflowDefinition {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismStateTransitionEvaluation id;

    @Column(name = "next_state_selection", nullable = false)
    private Boolean nextStateSelection;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public final PrismStateTransitionEvaluation getId() {
        return id;
    }

    public final void setId(PrismStateTransitionEvaluation id) {
        this.id = id;
    }

    public final Boolean isNextStateSelection() {
        return nextStateSelection;
    }

    public final void setNextStateSelection(Boolean nextStateSelection) {
        this.nextStateSelection = nextStateSelection;
    }

    @Override
    public final Scope getScope() {
        return scope;
    }

    @Override
    public final void setScope(Scope scope) {
        this.scope = scope;
    }

    public StateTransitionEvaluation withId(PrismStateTransitionEvaluation id) {
        this.id = id;
        return this;
    }

    public StateTransitionEvaluation withNextStateSelection(Boolean nextStateSelection) {
        this.nextStateSelection = nextStateSelection;
        return this;
    }

    public StateTransitionEvaluation withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
