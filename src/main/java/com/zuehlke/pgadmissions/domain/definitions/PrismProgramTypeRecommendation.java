package com.zuehlke.pgadmissions.domain.definitions;

public class PrismProgramTypeRecommendation {

    private PrismOpportunityType opportunityType;

    private Integer delayDuration;

    private PrismDurationUnit delayDurationUnit;

    private PrismProgramTypeRecommendationBaselineType delayBaselineType;

    public PrismProgramTypeRecommendation(PrismOpportunityType opportunityType, Integer delayDuration, PrismDurationUnit delayDurationUnit,
            PrismProgramTypeRecommendationBaselineType delayBaselineType) {
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

    public final PrismProgramTypeRecommendationBaselineType getDelayBaselineType() {
        return delayBaselineType;
    }

}
