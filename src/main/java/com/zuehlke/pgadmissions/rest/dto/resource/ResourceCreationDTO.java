package com.zuehlke.pgadmissions.rest.dto.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "scope")
@JsonSubTypes({
        @Type(value = ApplicationDTO.class, name = "RESUME"),
        @Type(value = ApplicationDTO.class, name = "APPLICATION"),
        @Type(value = ResourceOpportunityDTO.class, name = "PROJECT"),
        @Type(value = ResourceOpportunityDTO.class, name = "PROGRAM"),
        @Type(value = DepartmentDTO.class, name = "DEPARTMENT"),
        @Type(value = InstitutionDTO.class, name = "INSTITUTION")
})
public abstract class ResourceCreationDTO {

    public abstract ResourceDTO getParentResource();

    public abstract void setParentResource(ResourceDTO parentResource);

    public abstract Integer getWorkflowPropertyConfigurationVersion();

    public abstract void setWorkflowPropertyConfigurationVersion(Integer workflowPropertyConfigurationVersion);

}
