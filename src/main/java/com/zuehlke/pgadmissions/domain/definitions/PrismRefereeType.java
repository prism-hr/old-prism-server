package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismRefereeType {

    ACADEMIC(PrismDisplayPropertyDefinition.APPLICATION_REFEREE_TYPE_ACADEMIC), //
    EMPLOYER(PrismDisplayPropertyDefinition.APPLICATION_REFEREE_TYPE_EMPLOYER), //
    OTHER(PrismDisplayPropertyDefinition.SYSTEM_OTHER);

    private PrismDisplayPropertyDefinition displayPropertyDefinition;

    private PrismRefereeType(PrismDisplayPropertyDefinition displayPropertyDefinition) {
        this.displayPropertyDefinition = displayPropertyDefinition;
    }

    public final PrismDisplayPropertyDefinition getDisplayPropertyDefinition() {
        return displayPropertyDefinition;
    }

}
