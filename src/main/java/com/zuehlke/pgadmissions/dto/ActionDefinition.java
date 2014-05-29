package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.enums.PrismAction;

public class ActionDefinition {

    private PrismAction action;

    private Boolean raisesUrgentFlag;
    
    public ActionDefinition() {
    }

    public ActionDefinition(PrismAction action, Boolean raisesUrgentFlag) {
        this.action = action;
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public PrismAction getAction() {
        return action;
    }
    
    public void setAction(PrismAction action) {
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