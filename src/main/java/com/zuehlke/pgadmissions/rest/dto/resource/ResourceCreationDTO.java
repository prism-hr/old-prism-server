package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceContext;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
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

    private PrismResourceContext context;

    private PrismState initialState;

    private ResourceDTO parentResource;

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

    public PrismResourceContext getContext() {
        return context;
    }

    public PrismState getInitialState() {
        return initialState;
    }

    public void setInitialState(PrismState initialState) {
        this.initialState = initialState;
    }

    public void setContext(PrismResourceContext context) {
        this.context = context;
    }

    public ResourceDTO getParentResource() {
        return parentResource;
    }

    public void setParentResource(ResourceDTO parentResource) {
        this.parentResource = parentResource;
    }

}
