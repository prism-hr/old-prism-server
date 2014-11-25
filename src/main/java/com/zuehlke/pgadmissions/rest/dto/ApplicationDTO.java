package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ApplicationDTO extends ResourceDTO {

    @NotNull
    private PrismScope resourceScope;

    @NotNull
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
}
