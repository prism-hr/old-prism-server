package com.zuehlke.pgadmissions.dto;

public class ActionDefinition {

    private String action;

    private Boolean raisesUrgentFlag;
    
    public ActionDefinition() {
    }

    public ActionDefinition(String action, Boolean raisesUrgentFlag) {
        this.action = action;
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
    	this.action = action;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }
    
    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
    	this.raisesUrgentFlag = raisesUrgentFlag;
    }

    @Override
    public String toString() {
        return "ActionDefinition [action=" + action + ", raisesUrgentFlag=" + raisesUrgentFlag + "]";
    }

}