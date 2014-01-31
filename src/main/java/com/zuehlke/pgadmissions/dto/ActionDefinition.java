package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;

public class ActionDefinition {

    private ApplicationFormAction action;

    private Boolean raisesUrgentFlag;
    
    public ActionDefinition() {
    }

    public ActionDefinition(ApplicationFormAction action, Boolean raisesUrgentFlag) {
        this.action = action;
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public ApplicationFormAction getAction() {
        return action;
    }
    
    public void setAction(ApplicationFormAction action) {
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