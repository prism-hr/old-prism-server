package uk.co.alumeni.prism.rest.representation.resource;

import uk.co.alumeni.prism.rest.representation.action.ActionRepresentation;

public class ResourceRepresentationCreation extends ResourceRepresentationSimple {

    private ActionRepresentation completionAction;

    public ActionRepresentation getCompletionAction() {
        return completionAction;
    }

    public void setCompletionAction(ActionRepresentation completionAction) {
        this.completionAction = completionAction;
    }

}
