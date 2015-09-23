package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = ApplicationDTO.class, name = "APPLICATION"),
        @Type(value = ResourceOpportunityDTO.class, name = "PROJECT"),
        @Type(value = ResourceOpportunityDTO.class, name = "PROGRAM"),
        @Type(value = ResourceParentDTO.class, name = "DEPARTMENT"),
        @Type(value = InstitutionDTO.class, name = "INSTITUTION"),
        @Type(value = ResourceCreationDTO.class, name = "SIMPLE")
})
public class ResourceCreationDTO {

    private Integer id;

    @NotNull
    private PrismScope scope;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }

    public ResourceDTO getParentResource() {
        throw new UnsupportedOperationException();
    }

    public void setParentResource(ResourceDTO parentResource) {
        throw new UnsupportedOperationException();
    }

}
