package uk.co.alumeni.prism.domain.definitions;

public enum PrismEthnicity implements PrismLocalizableDefinition {

    WHITE_BRITISH, //
    WHITE_IRISH, //
    WHITE_GYPSY, //
    WHITE_OTHER, //
    MULTI_WHITE_BLACK_CARIBBEAN, //
    MULTI_WHITE_BLACK_AFRICAN, //
    MULTI_WHITE_ASIAN, //
    MULTI_OTHER, //
    ASIAN_INDIAN, //
    ASIAN_PAKISTANI, //
    ASIAN_BANGLADESHI, //
    ASIAN_CHINESE, //
    ASIAN_OTHER, //
    BLACK_AFRICAN, //
    BLACK_CARIBBEAN, //
    BLACK_OTHER, //
    ARAB, //
    OTHER;

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_ETHNICITY_" + this.name());
    }

}
