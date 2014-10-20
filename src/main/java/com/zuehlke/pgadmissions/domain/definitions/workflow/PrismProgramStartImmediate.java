package com.zuehlke.pgadmissions.domain.definitions.workflow;

import org.joda.time.LocalDate;

public class PrismProgramStartImmediate extends PrismProgramStartAbstract {

    private Integer weekStartDelay;

    public final Integer getWeekStartDelay() {
        return weekStartDelay;
    }
    
    public final void setWeekStartDelay(Integer weekStartDelay) {
        this.weekStartDelay = weekStartDelay;
    }
    
    public PrismProgramStartImmediate withWeekStartDelay(Integer weekStartDelay) {
        this.weekStartDelay = weekStartDelay;
        return this;
    }
    
    public PrismProgramStartImmediate withStartDay(Integer startDay) {
        setStartDay(startDay);
        return this;
    }
    
    @Override
    public LocalDate getRecommendedStartDate(LocalDate baseline) {
        return getImmediateStartDate(baseline, weekStartDelay);
    }
    
}
