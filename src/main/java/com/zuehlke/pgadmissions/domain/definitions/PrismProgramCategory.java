package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_CATEGORY_EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_CATEGORY_FUNDING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_CATEGORY_LEARNING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_CATEGORY_STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_CATEGORY_WORK;

public enum PrismProgramCategory {

    STUDY(true, false, true, PROGRAM_CATEGORY_STUDY), //
    FUNDING(false, true, true, PROGRAM_CATEGORY_FUNDING), //
    EXPERIENCE(false, true, false, PROGRAM_CATEGORY_EXPERIENCE), //
    WORK(false, true, false, PROGRAM_CATEGORY_WORK), //
    LEARNING(true, false, false, PROGRAM_CATEGORY_LEARNING);

    private boolean hasFee;

    private boolean hasPay;

    private boolean requireDuration;

    private PrismDisplayProperty displayProperty;

    private PrismProgramCategory(boolean hasFee, boolean hasPay, boolean requireDuration, PrismDisplayProperty displayProperty) {
        this.hasFee = hasFee;
        this.hasPay = hasPay;
        this.requireDuration = requireDuration;
        this.displayProperty = displayProperty;
    }

    public final boolean isHasFee() {
        return hasFee;
    }

    public final boolean isHasPay() {
        return hasPay;
    }

    public final boolean isRequireDuration() {
        return requireDuration;
    }

    public final PrismDisplayProperty getDisplayProperty() {
        return displayProperty;
    }

}
