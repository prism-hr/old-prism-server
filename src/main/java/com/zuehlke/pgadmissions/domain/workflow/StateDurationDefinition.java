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

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDuration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDurationEvaluation;

@Entity
@Table(name = "STATE_DURATION_DEFINITION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StateDurationDefinition extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismStateDuration id;

    @Column(name = "duration_evaluation", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismDurationEvaluation durationEvaluation;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public PrismStateDuration getId() {
        return id;
    }

    public void setId(PrismStateDuration id) {
        this.id = id;
    }

    public final PrismDurationEvaluation getDurationEvaluation() {
        return durationEvaluation;
    }

    public final void setDurationEvaluation(PrismDurationEvaluation durationEvaluation) {
        this.durationEvaluation = durationEvaluation;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public StateDurationDefinition withId(PrismStateDuration id) {
        this.id = id;
        return this;
    }

    public StateDurationDefinition withDurationEvaluation(PrismDurationEvaluation durationEvaluation) {
        this.durationEvaluation = durationEvaluation;
        return this;
    }

    public StateDurationDefinition withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
