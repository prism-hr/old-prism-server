package uk.co.alumeni.prism.rest.representation.action;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;

public class ActionOutcomeRepresentation {

    private ResourceRepresentationSimple resource;

    private ResourceRepresentationSimple transitionResource;

    private PrismAction transitionAction;

    public ResourceRepresentationSimple getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationSimple resource) {
        this.resource = resource;
    }

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

    public ActionOutcomeRepresentation withResource(final ResourceRepresentationSimple resource) {
        this.resource = resource;
        return this;
    }

    public ActionOutcomeRepresentation withTransitionResource(ResourceRepresentationSimple transitionResource) {
        this.transitionResource = transitionResource;
        return this;
    }

    public ActionOutcomeRepresentation withTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

}