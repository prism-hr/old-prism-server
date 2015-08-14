package com.zuehlke.pgadmissions.dto.resource;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceDTO {

    private PrismScope scope;

    private Integer id;

    private String name;

    public ResourceDTO(PrismScope scope, Integer id, String name) {
        this.scope = scope;
        this.id = id;
        this.name = name;
    }

    public PrismScope getScope() {
        return scope;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(scope, id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ResourceDTO other = (ResourceDTO) object;
        return Objects.equal(scope, other.getScope()) && Objects.equal(id, other.getId());
    }

}
