package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismResourceFamilyCreation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceFamilyCreationRepresentation {

    private PrismResourceFamilyCreation resourceFamilyCreation;
    
    private List<ResourceCreationRepresentation> resourceCreations;
    
    public ResourceFamilyCreationRepresentation(PrismResourceFamilyCreation resourceFamilyCreation) {
        this.resourceFamilyCreation = resourceFamilyCreation;
    }

    public PrismResourceFamilyCreation getResourceFamilyCreation() {
        return resourceFamilyCreation;
    }

    public void setResourceFamilyCreation(PrismResourceFamilyCreation resourceFamilyCreation) {
        this.resourceFamilyCreation = resourceFamilyCreation;
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
