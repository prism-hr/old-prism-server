package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;

public class ApplicationDTO implements ResourceCreationDTO {

    @Valid
    private ResourceDTO parentResource;

    @NotNull
    private Integer workflowPropertyConfigurationVersion;

    @Override
    public Integer getWorkflowPropertyConfigurationVersion() {
        return workflowPropertyConfigurationVersion;
    }

    @Override
    public void setWorkflowPropertyConfigurationVersion(Integer workflowPropertyConfigurationVersion) {
        this.workflowPropertyConfigurationVersion = workflowPropertyConfigurationVersion;
    }

    @Override
    public ResourceDTO getParentResource() {
        return parentResource;
    }

    @Override
    public void setParentResource(ResourceDTO parentResource) {
        this.parentResource = parentResource;
    }

}
