package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismEthnicity implements PrismLocalizableDefinition {

    ARAB, //
    BANGLADESHI, //
    INDIAN, //
    PAKISTANI, //
    CHINESE, //
    ASIAN_OTHER, //
    BLACK, //
    AFRICAN, //
    CARIBBEAN, //
    GYPSY, //
    MIXED_WHITE_ASIAN, //
    MIXED_WHITE_AFRICAN, //
    MIXED_WHITE_CARIBBEAN, //
    MIXED_OTHER, //
    OTHER,
    WHITE,
    UNDISCLOSED;

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_ETHNICITY_" + this.name());
    }
    
}
