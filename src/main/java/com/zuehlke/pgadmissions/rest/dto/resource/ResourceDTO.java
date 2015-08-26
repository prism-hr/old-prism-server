package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.constraints.NotNull;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceDTO {

    @NotNull
    private PrismScope scope;

    @NotNull
    private Integer id;

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

    public ResourceDTO withScope(PrismScope resourceScope) {
        this.scope = resourceScope;
        return this;
    }

    public ResourceDTO withId(Integer resourceId) {
        this.id = resourceId;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, scope);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ResourceDTO other = (ResourceDTO) object;
        return scope.equals(other.getScope()) && id.equals(other.getId());
    }
    
}
