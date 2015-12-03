package uk.co.alumeni.prism.rest.representation.state;

public class StateRepresentationExtended extends StateRepresentationSimple {

    private boolean parallelizable;

    public boolean isParallelizable() {
        return parallelizable;
    }

    public void setParallelizable(boolean parallelizable) {
        this.parallelizable = parallelizable;
    }

}
