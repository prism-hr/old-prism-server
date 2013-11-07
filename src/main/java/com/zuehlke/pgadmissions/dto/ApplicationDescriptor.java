package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.google.common.collect.Lists;

public class ApplicationDescriptor {
    
    private List<ActionDefinition> actionDefinitions = Lists.newArrayList();
    
    private Boolean needsToSeeUpdate;

    public ApplicationDescriptor() {
    }

    public List<ActionDefinition> getActionDefinitions() {
        return actionDefinitions;
    }

    public Boolean getNeedsToSeeUpdate() {
        return needsToSeeUpdate;
    }

    public void setNeedsToSeeUpdate(Boolean needsToSeeUpdate) {
        this.needsToSeeUpdate = needsToSeeUpdate;
    }

    public boolean isRequiresAttention() {
        for (ActionDefinition action : actionDefinitions) {
            if (action.getRaisesUrgentFlag()) {
                return true;
            }
        }
        return false;
    }

}
