package com.zuehlke.pgadmissions.dto;

public class ApplicationDescriptor {
    private ActionsDefinitions actionsDefinition;
    private Boolean needsToSeeUpdate; 
    
    public ApplicationDescriptor(){
    }

    public ActionsDefinitions getActionsDefinition() {
        return actionsDefinition;
    }

    public void setActionsDefinition(ActionsDefinitions actionsDefinitions) {
        this.actionsDefinition = actionsDefinitions;
    }

    public Boolean getNeedsToSeeUpdate() {
        return needsToSeeUpdate;
    }

    public void setNeedsToSeeUpdate(Boolean needsToSeeUpdate) {
        this.needsToSeeUpdate = needsToSeeUpdate;
    }

}
