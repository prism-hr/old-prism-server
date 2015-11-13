package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismOpportunityCategory implements PrismLocalizableDefinition {

    STUDY(false, false, false), //
    PERSONAL_DEVELOPMENT(false, false, true), //
    EXPERIENCE(true, false, true), //
    WORK(true, true, false);

    private boolean published;
    
    private boolean defaultPermanent;
    
    private boolean permittedOnCourse;

    private PrismOpportunityCategory(boolean published, boolean defaultPermanent, boolean permittedOnCourse) {
        this.published = published;
        this.defaultPermanent = defaultPermanent;
        this.permittedOnCourse = permittedOnCourse;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isDefaultPermanent() {
        return defaultPermanent;
    }

    public boolean isPermittedOnCourse() {
        return permittedOnCourse;
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_OPPORTUNITY_CATEGORY_" + name());
    }

}
