package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismOfferType implements PrismLocalizableDefinition {

    CONDITIONAL,
    UNCONDITIONAL;

    @Override
    public final PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("APPLICATION_OFFER_" + name());
    }

}
