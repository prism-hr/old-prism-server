package com.zuehlke.pgadmissions.domain.definitions;

public class PrismOpportunityTypeRecommendation {

    private PrismOpportunityType opportunityType;

    private Integer delayDuration;

    private PrismDurationUnit delayDurationUnit;

    private PrismOpportunityTypeRecommendationBaselineType delayBaselineType;

    public PrismOpportunityTypeRecommendation(PrismOpportunityType opportunityType, Integer delayDuration, PrismDurationUnit delayDurationUnit,
            PrismOpportunityTypeRecommendationBaselineType delayBaselineType) {
        this.opportunityType = opportunityType;
        this.delayDuration = delayDuration;
        this.delayDurationUnit = delayDurationUnit;
        this.delayBaselineType = delayBaselineType;
    }

    public final PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public final Integer getDelayDuration() {
        return delayDuration;
    }

    public final PrismDurationUnit getDelayDurationUnit() {
        return delayDurationUnit;
    }

    public final PrismOpportunityTypeRecommendationBaselineType getDelayBaselineType() {
        return delayBaselineType;
    }

}
