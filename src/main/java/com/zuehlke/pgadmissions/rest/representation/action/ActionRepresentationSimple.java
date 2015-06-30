package com.zuehlke.pgadmissions.rest.representation.action;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class ActionRepresentationSimple {

    private PrismAction id;

    private Boolean raisesUrgentFlag;

    private Boolean primaryState;

    public PrismAction getId() {
        return id;
    }

    public void setId(PrismAction id) {
        this.id = id;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public Boolean getPrimaryState() {
        return primaryState;
    }

    public void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

    public ActionRepresentationSimple withId(PrismAction id) {
        this.id = id;
        return this;
    }

    public ActionRepresentationSimple withRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
        return this;
    }

    public ActionRepresentationSimple withPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
        return this;
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
        ActionRepresentationExtended other = (ActionRepresentationExtended) object;
        return Objects.equal(getId(), other.getId());
    }

}
