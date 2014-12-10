package com.zuehlke.pgadmissions.domain.definitions;

public class PrismProgramTypeRecommendation {

    private PrismProgramType programType;

    private Integer delayDuration;

    private PrismDurationUnit delayDurationUnit;

    private PrismProgramTypeRecommendationBaselineType delayBaselineType;

    public PrismProgramTypeRecommendation(PrismProgramType programType, Integer delayDuration, PrismDurationUnit delayDurationUnit, PrismProgramTypeRecommendationBaselineType delayBaselineType) {
        this.programType = programType;
        this.delayDuration = delayDuration;
        this.delayDurationUnit = delayDurationUnit;
        this.delayBaselineType = delayBaselineType;
    }

    public final PrismProgramType getProgramType() {
        return programType;
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
