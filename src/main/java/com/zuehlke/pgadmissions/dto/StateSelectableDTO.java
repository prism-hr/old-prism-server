package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class StateSelectableDTO {

    private PrismState state;

    private Boolean parallelizable;

    public PrismState getState() {
        return state;
    }

    public void setState(PrismState state) {
        this.state = state;
    }

    public Boolean getParallelizable() {
        return parallelizable;
    }

    public void setParallelizable(Boolean parallelizable) {
        this.parallelizable = parallelizable;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(state);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        StateSelectableDTO other = (StateSelectableDTO) object;
        return Objects.equal(state, other.getState());
    }

}
