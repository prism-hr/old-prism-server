package com.zuehlke.pgadmissions.rest.representation;

import java.util.Map;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class CurrentUserRepresentation extends UserRepresentation {

    private Map<PrismScope, Boolean> resourceScopeVisibility;

    public Map<PrismScope, Boolean> getResourceScopeVisibility() {
        return resourceScopeVisibility;
    }

    public void setResourceScopeVisibility(Map<PrismScope, Boolean> resourceScopeVisibility) {
        this.resourceScopeVisibility = resourceScopeVisibility;
    }
}
