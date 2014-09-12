package com.zuehlke.pgadmissions.domain.definitions.workflow;

import org.joda.time.LocalDate;

public class PrismProgramTypeImmediate extends PrismProgramTypeConfiguration {

    private Integer weekStartDelay;

    public final Integer getWeekStartDelay() {
        return weekStartDelay;
    }
    
    public final void setWeekStartDelay(Integer weekStartDelay) {
        this.weekStartDelay = weekStartDelay;
    }
    
    public PrismProgramTypeImmediate withWeekStartDelay(Integer weekStartDelay) {
        this.weekStartDelay = weekStartDelay;
        return this;
    }
    
    public PrismProgramTypeImmediate withStartDay(Integer startDay) {
        setStartDay(startDay);
        return this;
    }
    
    @Override
    public LocalDate getRecommendedStartDate(LocalDate baseline) {
        return getImmediateStartDate(baseline, weekStartDelay);
    }
    
}
