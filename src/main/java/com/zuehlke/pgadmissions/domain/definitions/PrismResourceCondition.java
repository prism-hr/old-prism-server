package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;

public class PrismResourceCondition {

    private PrismActionCondition actionCondition;

    private boolean partnerMode;

    public PrismResourceCondition(PrismActionCondition actionCondition, boolean partnerMode) {
        this.actionCondition = actionCondition;
        this.partnerMode = partnerMode;
    }

    public PrismActionCondition getActionCondition() {
        return actionCondition;
    }

    public void setActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
    }

    public boolean isPartnerMode() {
        return partnerMode;
    }

    public void setPartnerMode(boolean partnerMode) {
        this.partnerMode = partnerMode;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(actionCondition);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final PrismResourceCondition other = (PrismResourceCondition) object;
        return Objects.equal(actionCondition, other.getActionCondition());
    }

}
