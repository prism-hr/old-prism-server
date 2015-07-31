package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismOpportunityCategory {

    STUDY(true, false),
    FUNDING(false, true),
    EXPERIENCE(false, true),
    WORK(false, true),
    LEARNING(true, false);

    private boolean hasFee;

    private boolean hasPay;

    PrismOpportunityCategory(boolean hasFee, boolean hasPay) {
        this.hasFee = hasFee;
        this.hasPay = hasPay;
    }

    public boolean isHasFee() {
        return hasFee;
    }

    public boolean isHasPay() {
        return hasPay;
    }

}
