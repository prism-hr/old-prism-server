package com.zuehlke.pgadmissions.domain.definitions.workflow;


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
    
}
