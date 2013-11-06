package com.zuehlke.pgadmissions.dto;

public class ActionDefinition {

    private String action;

    private Boolean raisesUrgentFlag;

    public ActionDefinition(String action, Boolean raisesUrgentFlag) {
        this.action = action;
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public String getAction() {
        return action;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    @Override
    public String toString() {
        return "ActionDefinition [action=" + action + ", raisesUrgentFlag=" + raisesUrgentFlag + "]";
    }

}
