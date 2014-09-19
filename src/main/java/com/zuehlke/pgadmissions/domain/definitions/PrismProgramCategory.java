package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismProgramCategory {

    STUDY(true, false, true),
    SCHOLARSHIP(false, true, true),
    WORK_EXPERIENCE(false, true, false),
    EMPLOYMENT(false, true, false),
    TRAINING(true, false, false);
    
    private boolean hasFee;
    
    private boolean hasPay;
    
    private boolean requireDuration;

    private PrismProgramCategory(boolean hasFee, boolean hasPay, boolean requireDuration) {
        this.hasFee = hasFee;
        this.hasPay = hasPay;
        this.requireDuration = requireDuration;
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
    
}
