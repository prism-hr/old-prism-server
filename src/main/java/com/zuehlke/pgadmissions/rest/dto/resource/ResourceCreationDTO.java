package com.zuehlke.pgadmissions.rest.dto.resource;

public interface ResourceCreationDTO {

    public ResourceDTO getParentResource();

    public void setParentResource(ResourceDTO parentResource);

    public Integer getWorkflowPropertyConfigurationVersion();

    public void setWorkflowPropertyConfigurationVersion(Integer workflowPropertyConfigurationVersion);

}
