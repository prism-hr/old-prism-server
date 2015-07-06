package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceDTO {
    
    private PrismScope resourceScope;

    private Integer resourceId;

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }
    
    public ResourceDTO withResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
        return this;
    }
    
    public ResourceDTO withResourceId(Integer resourceId) {
        this.resourceId = resourceId;
        return this;
    }

}
