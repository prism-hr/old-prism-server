package com.zuehlke.pgadmissions.domain;

import com.google.common.base.Objects;

public abstract class WorkflowResource implements IUniqueEntity {

    public abstract Object getId();

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final WorkflowResource otherResource = (WorkflowResource) object;
        return Objects.equal(getId(), otherResource.getId());
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("id", getId());
    }

}
