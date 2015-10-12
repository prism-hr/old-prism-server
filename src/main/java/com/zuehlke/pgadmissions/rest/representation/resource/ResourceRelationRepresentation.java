package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismScopeRelationContext;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceRelationRepresentation {

    private PrismScopeRelationContext context;

    private List<ResourceRelationComponentRepresentation> resourceRelations;

    public ResourceRelationRepresentation(PrismScopeRelationContext context) {
        this.context = context;
    }

    public PrismScopeRelationContext getContext() {
        return context;
    }

    public void setContext(PrismScopeRelationContext creationContext) {
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

        private PrismOpportunityCategory[] categories;

        private Boolean autosuggest;

        private Boolean description;

        private Boolean user;

        private Boolean required;

        public ResourceRelationComponentRepresentation(PrismScope resourceScope, Boolean autosuggest, Boolean description, Boolean user, PrismOpportunityCategory... categories) {
            this.resourceScope = resourceScope;
            this.categories = categories;
            this.autosuggest = autosuggest;
            this.description = description;
            this.user = user;
        }

        public PrismScope getResourceScope() {
            return resourceScope;
        }

        public void setResourceScope(PrismScope resourceScope) {
            this.resourceScope = resourceScope;
        }

        public PrismOpportunityCategory[] getCategories() {
            return categories;
        }

        public void setCategories(PrismOpportunityCategory[] categories) {
            this.categories = categories;
        }

        public Boolean getAutosuggest() {
            return autosuggest;
        }

        public void setAutosuggest(Boolean autosuggest) {
            this.autosuggest = autosuggest;
        }

        public Boolean getDescription() {
            return description;
        }

        public void setDescription(Boolean description) {
            this.description = description;
        }

        public Boolean getUser() {
            return user;
        }

        public void setUser(Boolean user) {
            this.user = user;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

    }

}
