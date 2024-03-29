package uk.co.alumeni.prism.domain.definitions;

public enum PrismGender implements PrismLocalizableDefinition {

    FEMALE, //
    MALE, //
    INDETERMINATE;

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_GENDER_" + this.name());
    }

}
