package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;

public class StateChangeDTO {
    
    private Resource resource;
    
    private State state;
    
    private Action action;
    
    public final Resource getResource() {
        return resource;
    }

    public final void setResource(Resource resource) {
        this.resource = resource;
    }

    public final State getState() {
        return state;
    }

    public final void setState(State state) {
        this.state = state;
    }

    public final Action getAction() {
        return action;
    }

    public final void setAction(Action action) {
        this.action = action;
    }

}
