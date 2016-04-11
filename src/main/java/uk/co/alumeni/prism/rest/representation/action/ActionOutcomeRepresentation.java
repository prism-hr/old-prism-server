package uk.co.alumeni.prism.rest.representation.action;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;

public class ActionOutcomeRepresentation {

    private ResourceRepresentationSimple resource;

    private ResourceRepresentationSimple transitionResource;

    private PrismAction action;

    private PrismAction transitionAction;

    private ActionOutcomeReplicableRepresentation replicable;

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

    public PrismAction getAction() {
        return action;
    }

    public void setAction(PrismAction action) {
        this.action = action;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public void setTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
    }

    public ActionOutcomeReplicableRepresentation getReplicable() {
        return replicable;
    }

    public void setReplicable(ActionOutcomeReplicableRepresentation replicable) {
        this.replicable = replicable;
    }

    public ActionOutcomeRepresentation withResource(ResourceRepresentationSimple resource) {
        this.resource = resource;
        return this;
    }

    public ActionOutcomeRepresentation withTransitionResource(ResourceRepresentationSimple transitionResource) {
        this.transitionResource = transitionResource;
        return this;
    }

    public ActionOutcomeRepresentation withAction(PrismAction action) {
        this.action = action;
        return this;
    }

    public ActionOutcomeRepresentation withTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public ActionOutcomeRepresentation withReplicable(final ActionOutcomeReplicableRepresentation replicable) {
        this.replicable = replicable;
        return this;
    }

}
