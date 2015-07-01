package com.zuehlke.pgadmissions.rest.representation.state;

public class StateRepresentationExtended extends StateRepresentationSimple {

    private boolean parallelizable;

    public boolean isParallelizable() {
        return parallelizable;
    }

    public void setParallelizable(boolean parallelizable) {
        this.parallelizable = parallelizable;
    }

}
