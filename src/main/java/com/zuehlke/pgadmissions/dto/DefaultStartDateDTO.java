package com.zuehlke.pgadmissions.dto;

import org.joda.time.LocalDate;

public class DefaultStartDateDTO {

    private LocalDate immediate;
    
    private LocalDate scheduled;

    public final LocalDate getImmediate() {
        return immediate;
    }

    public final LocalDate getScheduled() {
        return scheduled;
    }
    
    public DefaultStartDateDTO withImmediate(LocalDate immediate) {
        this.immediate = immediate;
        return this;
    }
    
    public DefaultStartDateDTO withScheduled(LocalDate scheduled) {
        this.scheduled = scheduled;
        return this;
    }
    
}
