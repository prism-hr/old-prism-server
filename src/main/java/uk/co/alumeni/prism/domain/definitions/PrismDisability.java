package uk.co.alumeni.prism.domain.definitions;

public enum PrismDisability implements PrismLocalizableDefinition {

    AUTISM, //
    HEARING, //
    LEARNING, //
    MENTAL, //
    MOBILITY, //
    MULTIPLE, //
    OTHER, //
    UNSEEN, //
    VISION;

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_DISABILITY_" + this.name());
    }

}