package com.zuehlke.pgadmissions.domain.definitions.workflow;

import org.joda.time.LocalDate;

public class PrismProgramTypeStartScheduled extends PrismProgramTypeStartConfiguration {

    private Integer startMonth;
    
    private Integer startWeek;

    public final Integer getStartMonth() {
        return startMonth;
    }

    public final void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

    public final Integer getStartWeek() {
        return startWeek;
    }

    public final void setStartWeek(Integer startWeek) {
        this.startWeek = startWeek;
    }
    
    public PrismProgramTypeStartScheduled withStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
        return this;
    }
    
    public PrismProgramTypeStartScheduled withStartWeek(Integer startWeek) {
        this.startWeek = startWeek;
        return this;
    }
    
    public PrismProgramTypeStartScheduled withStartDay(Integer startDay) {
        setStartDay(startDay);
        return this;
    }

    @Override
    public LocalDate getRecommendedStartDate(LocalDate baseline) {
        LocalDate startDate = new LocalDate().withYear(baseline.getYear()).withMonthOfYear(startMonth).plusWeeks(startWeek).withDayOfWeek(getStartDay());
        return startDate.isAfter(new LocalDate()) ? startDate.plusYears(1) : startDate;
    }
    
}
