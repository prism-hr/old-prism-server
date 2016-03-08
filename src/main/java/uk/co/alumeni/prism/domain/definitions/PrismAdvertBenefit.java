package uk.co.alumeni.prism.domain.definitions;

public enum PrismAdvertBenefit implements PrismLocalizableDefinition {

    BONUS, //
    ON_TARGET_EARNING, //
    SHARE_OPTION, //
    HEALTH_INSURANCE, //
    PENSION, //
    OTHER;

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_ADVERT_BENEFIT_" + name());
    }

}
