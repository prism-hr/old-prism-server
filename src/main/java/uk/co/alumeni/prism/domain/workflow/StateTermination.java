package uk.co.alumeni.prism.domain.workflow;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationEvaluation;

import javax.persistence.*;

@Entity
@Table(name = "state_termination", uniqueConstraints = {@UniqueConstraint(columnNames = {"state_transition_id", "termination_state_id"})})
public class StateTermination implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_transition_id", nullable = false)
    private StateTransition stateTransition;

    @ManyToOne
    @JoinColumn(name = "termination_state_id", nullable = false)
    private State terminationState;

    @Column(name = "state_termination_evaluation")
    @Enumerated(EnumType.STRING)
    private PrismStateTerminationEvaluation stateTerminationEvaluation;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final StateTransition getStateTransition() {
        return stateTransition;
    }

    public final void setStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
    }

    public final State getTerminationState() {
        return terminationState;
    }

    public final void setTerminationState(State terminationState) {
        this.terminationState = terminationState;
    }

    public final PrismStateTerminationEvaluation getStateTerminationEvaluation() {
        return stateTerminationEvaluation;
    }

    public final void setStateTerminationEvaluation(PrismStateTerminationEvaluation stateTerminationEvaluation) {
        this.stateTerminationEvaluation = stateTerminationEvaluation;
    }

    public StateTermination withStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
        return this;
    }

    public StateTermination withTerminationState(State terminationState) {
        this.terminationState = terminationState;
        return this;
    }

    public StateTermination withStateTerminationEvaluation(PrismStateTerminationEvaluation stateTerminationEvaluation) {
        this.stateTerminationEvaluation = stateTerminationEvaluation;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("stateTransition", stateTransition).addProperty("terminationState", terminationState);
    }

}
