package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class DeferredStateTransitionDTO implements Comparable<DeferredStateTransitionDTO> {

    private Class<? extends Resource> resourceClass;

    private Integer resourceId;

    private PrismAction actionId;

    public final Class<? extends Resource> getResourceClass() {
        return resourceClass;
    }
    
    public DeferredStateTransitionDTO(Class<? extends Resource> resourceClass, Integer resourceId, PrismAction actionId) {
        this.resourceClass = resourceClass;
        this.resourceId = resourceId;
        this.actionId = actionId;
    }

    public final Integer getResourceId() {
        return resourceId;
    }

    public final PrismAction getActionId() {
        return actionId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resourceClass, resourceId);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final DeferredStateTransitionDTO other = (DeferredStateTransitionDTO) object;
        return Objects.equal(resourceClass, other.getResourceClass()) && Objects.equal(resourceId, other.getResourceId());
    }

    @Override
    public int compareTo(DeferredStateTransitionDTO other) {
        if (this.equals(other)) {
            return 0;
        } else if (PrismScope.getResourceScope(resourceClass).getPrecedence() < PrismScope.getResourceScope(other.getResourceClass()).getPrecedence()) {
            return -1;
        } else if (resourceId < other.getResourceId()) {
            return -1;
        } else {
            return 1;
        }
    }
    
}
