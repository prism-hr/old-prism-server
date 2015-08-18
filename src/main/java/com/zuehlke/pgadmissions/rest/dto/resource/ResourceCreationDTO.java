package com.zuehlke.pgadmissions.rest.dto.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "scope")
@JsonSubTypes({
        @Type(value = ApplicationDTO.class, name = "APPLICATION"),
        @Type(value = ResourceOpportunityDTO.class, name = "PROJECT"),
        @Type(value = ResourceOpportunityDTO.class, name = "PROGRAM"),
        @Type(value = DepartmentDTO.class, name = "DEPARTMENT"),
        @Type(value = InstitutionDTO.class, name = "INSTITUTION")
})
public abstract class ResourceCreationDTO {

    private PrismScope scope;

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }

    public abstract ResourceDTO getParentResource();

    public abstract void setParentResource(ResourceDTO parentResource);

    public abstract Integer getWorkflowPropertyConfigurationVersion();

    public abstract void setWorkflowPropertyConfigurationVersion(Integer workflowPropertyConfigurationVersion);

}
