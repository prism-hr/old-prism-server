package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismScopeCreation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import java.util.List;

public class ResourceFamilyCreationRepresentation {

    private PrismScopeCreation creationContext;

    private List<ResourceCreationRepresentation> resourceCreations;

    public ResourceFamilyCreationRepresentation(PrismScopeCreation creationContext) {
        this.creationContext = creationContext;
    }

    public PrismScopeCreation getCreationContext() {
        return creationContext;
    }

    public void setCreationContext(PrismScopeCreation creationContext) {
        this.creationContext = creationContext;
    }

    public List<ResourceCreationRepresentation> getResourceCreations() {
        return resourceCreations;
    }

    public void setResourceCreations(List<ResourceCreationRepresentation> resourceCreations) {
        this.resourceCreations = resourceCreations;
    }

    public static class ResourceCreationRepresentation {

        private PrismScope resourceScope;

        private boolean required;

        public ResourceCreationRepresentation(PrismScope resourceScope) {
            this.resourceScope = resourceScope;
        }

        public PrismScope getResourceScope() {
            return resourceScope;
        }

        public void setResourceScope(PrismScope resourceScope) {
            this.resourceScope = resourceScope;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

    }

}
