package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.base.Objects;

public class PrismStateActionAssignment {

    private PrismRole role;

    private PrismActionEnhancement actionEnhancement;

    public PrismRole getRole() {
        return role;
    }

    public PrismActionEnhancement getActionEnhancement() {
        return actionEnhancement;
    }

    public PrismStateActionAssignment withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public PrismStateActionAssignment withActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(role, actionEnhancement);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final PrismStateActionAssignment other = (PrismStateActionAssignment) object;
        return Objects.equal(role, other.getRole()) && Objects.equal(actionEnhancement, other.getActionEnhancement());
    }

}
