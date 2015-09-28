package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceRelationRepresentation {

    private PrismMotivationContext context;

    private List<ResourceRelationComponentRepresentation> resourceRelations;

    public ResourceRelationRepresentation(PrismMotivationContext context) {
        this.context = context;
    }

    public PrismMotivationContext getContext() {
        return context;
    }

    public void setContext(PrismMotivationContext creationContext) {
        this.context = creationContext;
    }

    public List<ResourceRelationComponentRepresentation> getResourceRelations() {
        return resourceRelations;
    }

    public void setResourceRelations(List<ResourceRelationComponentRepresentation> resourceRelations) {
        this.resourceRelations = resourceRelations;
    }

    public static class ResourceRelationComponentRepresentation {

        private PrismScope resourceScope;

        private boolean required;

        public ResourceRelationComponentRepresentation(PrismScope resourceScope) {
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
