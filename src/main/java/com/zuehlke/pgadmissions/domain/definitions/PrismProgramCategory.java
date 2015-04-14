package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismProgramCategory {

    STUDY(true, false, true),
    FUNDING(false, true, true),
    EXPERIENCE(false, true, false),
    WORK(false, true, false),
    LEARNING(true, false, false);

    private boolean hasFee;

    private boolean hasPay;

    private boolean requireDuration;

    PrismProgramCategory(boolean hasFee, boolean hasPay, boolean requireDuration) {
        this.hasFee = hasFee;
        this.hasPay = hasPay;
        this.requireDuration = requireDuration;
    }

    public boolean isHasFee() {
        return hasFee;
    }

    public boolean isHasPay() {
        return hasPay;
    }

    public boolean isRequireDuration() {
        return requireDuration;
    }

}
