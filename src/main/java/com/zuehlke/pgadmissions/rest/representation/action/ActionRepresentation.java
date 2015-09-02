package com.zuehlke.pgadmissions.rest.representation.action;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;

public class ActionRepresentation {

    private PrismAction id;

    private PrismActionCategory category;
    
    private Boolean declinable;

    public PrismAction getId() {
        return id;
    }

    public void setId(PrismAction id) {
        this.id = id;
    }

    public PrismActionCategory getCategory() {
        return category;
    }

    public void setCategory(PrismActionCategory category) {
        this.category = category;
    }

    public Boolean getDeclinable() {
        return declinable;
    }

    public void setDeclinable(Boolean declinable) {
        this.declinable = declinable;
    }

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
        ActionRepresentation other = (ActionRepresentation) object;
        return Objects.equal(getId(), other.getId());
    }

}
