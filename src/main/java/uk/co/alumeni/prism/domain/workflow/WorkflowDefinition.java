package uk.co.alumeni.prism.domain.workflow;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.UniqueEntity;

public abstract class WorkflowDefinition implements UniqueEntity {

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
        WorkflowDefinition other = (WorkflowDefinition) object;
        return Objects.equal(getId(), other.getId());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("id", getId());
    }

    @Override
    public String toString() {
        return getId().name();
    }

}
