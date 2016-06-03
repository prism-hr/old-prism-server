package uk.co.alumeni.prism.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition;

@Entity
@Table(name = "state_duration_definition")
public class StateDurationDefinition extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismStateDurationDefinition id;

    @Column(name = "escalation", nullable = false)
    private Boolean escalation;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public PrismStateDurationDefinition getId() {
        return id;
    }

    public void setId(PrismStateDurationDefinition id) {
        this.id = id;
    }

    public final Boolean getEscalation() {
        return escalation;
    }

    public final void setEscalation(Boolean escalation) {
        this.escalation = escalation;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public StateDurationDefinition withId(PrismStateDurationDefinition id) {
        this.id = id;
        return this;
    }

    public StateDurationDefinition withEscalation(Boolean escalation) {
        this.escalation = escalation;
        return this;
    }

    public StateDurationDefinition withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
