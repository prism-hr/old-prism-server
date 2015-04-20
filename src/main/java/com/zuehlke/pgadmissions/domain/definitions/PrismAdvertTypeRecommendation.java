package com.zuehlke.pgadmissions.domain.definitions;

public class PrismAdvertTypeRecommendation {

    private PrismAdvertType advertType;

    private Integer delayDuration;

    private PrismDurationUnit delayDurationUnit;

    private PrismAdvertTypeRecommendationBaselineType delayBaselineType;

    public PrismAdvertTypeRecommendation(PrismAdvertType advertType, Integer delayDuration, PrismDurationUnit delayDurationUnit,
            PrismAdvertTypeRecommendationBaselineType delayBaselineType) {
        this.advertType = advertType;
        this.delayDuration = delayDuration;
        this.delayDurationUnit = delayDurationUnit;
        this.delayBaselineType = delayBaselineType;
    }

    public final PrismAdvertType getAdvertType() {
        return advertType;
    }

    public final Integer getDelayDuration() {
        return delayDuration;
    }

    public final PrismDurationUnit getDelayDurationUnit() {
        return delayDurationUnit;
    }

    public final PrismAdvertTypeRecommendationBaselineType getDelayBaselineType() {
        return delayBaselineType;
    }

}
