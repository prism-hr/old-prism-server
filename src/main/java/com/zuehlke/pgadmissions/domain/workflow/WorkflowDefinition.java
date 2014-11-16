package com.zuehlke.pgadmissions.domain.workflow;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;

public abstract class WorkflowDefinition implements IUniqueEntity {

    public abstract Enum<?> getId();

    public abstract Scope getScope();

    public abstract void setScope(Scope scope);

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
        final WorkflowDefinition otherResource = (WorkflowDefinition) object;
        return Objects.equal(getId(), otherResource.getId());
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("id", getId());
    }

}
