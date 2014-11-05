package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import javax.validation.constraints.NotNull;

public class ApplicationDTO {

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
