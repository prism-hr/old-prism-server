package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.enums.SystemAction;

public class ActionDefinition {

    private SystemAction action;

    private Boolean raisesUrgentFlag;
    
    public ActionDefinition() {
    }

    public ActionDefinition(SystemAction action, Boolean raisesUrgentFlag) {
        this.action = action;
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public SystemAction getAction() {
        return action;
    }
    
    public void setAction(SystemAction action) {
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