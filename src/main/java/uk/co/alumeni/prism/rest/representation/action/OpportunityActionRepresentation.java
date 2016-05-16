package uk.co.alumeni.prism.rest.representation.action;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationIdentity;

public class OpportunityActionRepresentation {

    private PrismAction id;

    private PrismActionCategory category;

    private ResourceRepresentationIdentity resource;

    public PrismAction getId() {
        return id;
    }

    public void setId(PrismAction id) {
        this.id = id;
    }

    public PrismActionCategory getCategory() {
        return category;
    }

    public void setCategory(PrismActionCategory category) {
        this.category = category;
    }

    public ResourceRepresentationIdentity getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationIdentity resource) {
        this.resource = resource;
    }

    public OpportunityActionRepresentation withResource(final ResourceRepresentationIdentity resource) {
        this.resource = resource;
        return this;
    }

    public OpportunityActionRepresentation withCategory(final PrismActionCategory category) {
        this.category = category;
        return this;
    }

    public OpportunityActionRepresentation withId(final PrismAction id) {
        this.id = id;
        return this;
    }


}
