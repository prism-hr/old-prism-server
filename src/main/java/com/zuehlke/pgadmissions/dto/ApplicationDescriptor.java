package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.google.common.collect.Lists;

public class ApplicationDescriptor {
	
	private Boolean needsToSeeUrgentFlag;
	
    private Boolean needsToSeeUpdateFlag;
    
    private List<ActionDefinition> actionDefinitions = Lists.newArrayList();

    public ApplicationDescriptor() {
    }

    public List<ActionDefinition> getActionDefinitions() {
        return actionDefinitions;
    }

    public Boolean getNeedsToSeeUpdateFlag() {
        return needsToSeeUpdateFlag;
    }

    public void setNeedsToSeeUpdateFlag(Boolean needsToSeeUpdateFlag) {
        this.needsToSeeUpdateFlag = needsToSeeUpdateFlag;
    }

    public Boolean getNeedsToSeeUrgentFlag() {
    	return needsToSeeUrgentFlag;
    }

    public void setNeedsToSeeUrgentFlag(Boolean needsToSeeUrgentFlag) {
    	this.needsToSeeUrgentFlag = needsToSeeUrgentFlag;
    }
}