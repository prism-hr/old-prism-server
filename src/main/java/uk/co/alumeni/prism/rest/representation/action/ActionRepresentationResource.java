package uk.co.alumeni.prism.rest.representation.action;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationIdentity;

public class ActionRepresentationResource extends ActionRepresentation {

    private ResourceRepresentationIdentity resource;

    public ResourceRepresentationIdentity getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationIdentity resource) {
        this.resource = resource;
    }

    public ActionRepresentationResource withId(PrismAction id) {
        setId(id);
        return this;
    }

    public ActionRepresentationResource withCategory(PrismActionCategory category) {
        setCategory(category);
        return this;
    }

    public ActionRepresentationResource withResource(ResourceRepresentationIdentity resource) {
        this.resource = resource;
        return this;
    }

}
