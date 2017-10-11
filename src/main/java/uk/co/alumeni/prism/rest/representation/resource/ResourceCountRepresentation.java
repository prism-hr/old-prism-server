package uk.co.alumeni.prism.rest.representation.resource;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

public class ResourceCountRepresentation {

    private PrismScope resourceScope;

    private Integer resourceCount;

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }

    public Integer getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(Integer resourceCount) {
        this.resourceCount = resourceCount;
    }

    public ResourceCountRepresentation withResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
        return this;
    }

    public ResourceCountRepresentation withResourceCount(Integer resourceCount) {
        this.resourceCount = resourceCount;
        return this;
    }

}
