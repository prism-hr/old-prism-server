package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentation;

public class ResourceRepresentationCreation extends ResourceRepresentationSimple {
    
    private ActionRepresentation completionAction;

    public ActionRepresentation getCompletionAction() {
        return completionAction;
    }

    public void setCompletionAction(ActionRepresentation completionAction) {
        this.completionAction = completionAction;
    }
    
}
