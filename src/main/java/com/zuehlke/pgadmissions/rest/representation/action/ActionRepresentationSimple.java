package com.zuehlke.pgadmissions.rest.representation.action;

public class ActionRepresentationSimple extends ActionRepresentation {

    private Boolean raisesUrgentFlag;

    private Boolean primaryState;

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public Boolean getPrimaryState() {
        return primaryState;
    }

    public void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

}
