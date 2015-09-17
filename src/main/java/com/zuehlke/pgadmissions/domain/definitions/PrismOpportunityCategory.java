package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismOpportunityCategory implements PrismLocalizableDefinition {

    STUDY(false), //
    EXPERIENCE(true), //
    WORK(true);

    private boolean published;

    private PrismOpportunityCategory(boolean published) {
        this.published = published;
    }

    public boolean isPublished() {
        return published;
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_OPPORTUNITY_CATEGORY_" + name());
    }

}
