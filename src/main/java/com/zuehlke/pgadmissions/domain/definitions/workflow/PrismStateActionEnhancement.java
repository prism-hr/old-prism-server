package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.base.Objects;

public class PrismStateActionEnhancement {
    
    private PrismEnhancementType enhancement;
    
    private PrismAction delegatedAction;

    public PrismEnhancementType getEnhancement() {
        return enhancement;
    }

    public PrismAction getDelegatedAction() {
        return delegatedAction;
    }
    
    public PrismStateActionEnhancement withEnhancement(PrismEnhancementType enhancement) {
        this.enhancement = enhancement;
        return this;
    }
    
    public PrismStateActionEnhancement withDelegatedAction(PrismAction delegatedAction) {
        this.delegatedAction = delegatedAction;
        return this;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(enhancement, delegatedAction);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PrismStateActionEnhancement other = (PrismStateActionEnhancement) obj;
        return Objects.equal(enhancement, other.getEnhancement()) && Objects.equal(delegatedAction, other.getDelegatedAction());
    }
    
}
