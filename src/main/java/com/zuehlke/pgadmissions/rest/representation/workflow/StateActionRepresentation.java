package com.zuehlke.pgadmissions.rest.representation.workflow;

public class StateActionRepresentation {

    private String state;

    private ActionRepresentation action;

    private boolean raisesUrgentFlag;

    private String displayName;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ActionRepresentation getAction() {
        return action;
    }

    public void setAction(ActionRepresentation action) {
        this.action = action;
    }

    public boolean isRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
