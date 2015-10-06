package com.zuehlke.pgadmissions.domain.resource;

import org.joda.time.LocalDate;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResourceExecution;

public abstract class ResourceStateDefinition extends WorkflowResourceExecution {

    public abstract State getState();

    public abstract void setState(State state);

    public abstract Boolean getPrimaryState();

    public abstract void setPrimaryState(Boolean primaryState);
    
    public abstract LocalDate getCreatedDate();
    
    public abstract void setCreatedDate(LocalDate createdDate);

    @Override
    public int hashCode() {
        return Objects.hashCode(getResource(), getState());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ResourceStateDefinition other = (ResourceStateDefinition) object;
        return Objects.equal(getResource(), other.getResource()) && Objects.equal(getState(), other.getState());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("state", getState());
    }

}
