package uk.co.alumeni.prism.domain.definitions.workflow;

import com.google.common.base.Objects;

import static com.google.common.base.Objects.equal;

public class PrismStateTermination {

    private PrismState terminationState;

    private PrismStateTerminationEvaluation stateTerminationEvaluation;

    public final PrismState getTerminationState() {
        return terminationState;
    }

    public final PrismStateTerminationEvaluation getStateTerminationEvaluation() {
        return stateTerminationEvaluation;
    }

    public PrismStateTermination withTerminationState(PrismState terminationState) {
        this.terminationState = terminationState;
        return this;
    }

    public PrismStateTermination withStateTerminationEvaluation(PrismStateTerminationEvaluation stateTerminationEvaluation) {
        this.stateTerminationEvaluation = stateTerminationEvaluation;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(terminationState);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        PrismStateTermination other = (PrismStateTermination) object;
        return equal(terminationState, other.getTerminationState()) && equal(stateTerminationEvaluation, other.getStateTerminationEvaluation());
    }

}
