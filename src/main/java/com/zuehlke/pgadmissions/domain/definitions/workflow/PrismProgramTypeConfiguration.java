package com.zuehlke.pgadmissions.domain.definitions.workflow;

import org.joda.time.LocalDate;

public abstract class PrismProgramTypeConfiguration {
    
    private Integer startDay;
    
    private static final Integer DEFUALT_WEEK_START_DELAY = 4;

    public final Integer getStartDay() {
        return startDay;
    }

    public final void setStartDay(Integer startDay) {
        this.startDay = startDay;
    }
    
    public boolean isImmediateStart() {
        return this.getClass() == PrismProgramTypeImmediate.class;
    }

    public LocalDate getImmediateStartDate() {
        return getImmediateStartDate(new LocalDate());
    }
    
    public LocalDate getImmediateStartDate(LocalDate baseline) {
        return getImmediateStartDate(baseline, DEFUALT_WEEK_START_DELAY);
    }
    
    public LocalDate getImmediateStartDate(LocalDate baseline, Integer weekStartDelay) {
        return baseline.plusWeeks(weekStartDelay).withDayOfWeek(getStartDay());
    }
    
    public LocalDate getRecommendedStartDate() {
        return getRecommendedStartDate(new LocalDate());
    }
    
    public abstract LocalDate getRecommendedStartDate(LocalDate baseline);
    
}
