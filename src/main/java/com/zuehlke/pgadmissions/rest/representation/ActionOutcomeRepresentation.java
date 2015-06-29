package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

public class ActionOutcomeRepresentation {

    private ResourceRepresentationSimple transitionResource;

    private PrismAction transitionAction;

    public ResourceRepresentationSimple getTransitionResource() {
        return transitionResource;
    }

    public void setTransitionResource(ResourceRepresentationSimple transitionResource) {
        this.transitionResource = transitionResource;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public void setTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
    }

}
