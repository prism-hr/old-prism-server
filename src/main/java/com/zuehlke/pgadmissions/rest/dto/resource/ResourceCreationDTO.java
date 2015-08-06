package com.zuehlke.pgadmissions.rest.dto.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "scope")
@JsonSubTypes({
        @Type(value = ApplicationDTO.class, name = "APPLICATION"),
        @Type(value = ResourceOpportunityDTO.class, name = "PROJECT"),
        @Type(value = ResourceOpportunityDTO.class, name = "PROGRAM"),
        @Type(value = ResourceParentDivisionDTO.class, name = "DEPARTMENT"),
        @Type(value = InstitutionDTO.class, name = "INSTITUTION")
})
public interface ResourceCreationDTO {

    ResourceDTO getParentResource();

    void setParentResource(ResourceDTO parentResource);

    Integer getWorkflowPropertyConfigurationVersion();

    void setWorkflowPropertyConfigurationVersion(Integer workflowPropertyConfigurationVersion);

}
