package uk.co.alumeni.prism.rest.dto.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.rest.dto.application.ApplicationDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

    @NotNull
    private PrismScope scope;

    private Integer id;

    private PrismResourceContext context;

    private PrismState initialState;

    @Valid
    private ResourceDTO parentResource;

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public ResourceCreationDTO withScope(PrismScope scope) {
        this.scope = scope;
        return this;
    }

    public ResourceCreationDTO withId(Integer id) {
        this.id = id;
        return this;
    }

}
