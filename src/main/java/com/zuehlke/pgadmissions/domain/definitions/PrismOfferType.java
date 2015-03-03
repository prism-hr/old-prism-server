package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_OFFER_CONDITIONAL;

public enum PrismOfferType {

    CONDITIONAL(APPLICATION_OFFER_CONDITIONAL),
    UNCONDITIONAL(PrismDisplayPropertyDefinition.APPLICATION_OFFER_UNCONDITIONAL);
    
    private PrismDisplayPropertyDefinition displayProperty;

    private PrismOfferType(PrismDisplayPropertyDefinition displayProperty) {
        this.displayProperty = displayProperty;
    }

    public final PrismDisplayPropertyDefinition getDisplayProperty() {
        return displayProperty;
    }
    
}
