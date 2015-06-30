package com.zuehlke.pgadmissions.rest.representation.state;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class StateRepresentationExtended extends StateRepresentationSimple {

    private boolean parallelizable;

    public boolean isParallelizable() {
        return parallelizable;
    }

    public void setParallelizable(boolean parallelizable) {
        this.parallelizable = parallelizable;
    }

    public StateRepresentationExtended withState(PrismState state) {
        setState(state);
        return this;
    }

    public StateRepresentationExtended withStateGroup(PrismStateGroup stateGroup) {
        setStateGroup(stateGroup);
        return this;
    }
    
    public StateRepresentationExtended withParallelizable(boolean parallelizable) {
        this.parallelizable = parallelizable;
        return this;
    }    

}
