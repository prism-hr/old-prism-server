package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismStudyOption implements PrismLocalizableDefinition {

    FULL_TIME,
    PART_TIME,
    MODULAR_FLEXIBLE;

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_STUDY_OPTION_" + name());
    }

}
