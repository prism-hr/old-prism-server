package com.zuehlke.pgadmissions.rest.representation.resource;

import org.joda.time.LocalDate;

public class ResourceOpportunityRepresentationSimple extends ResourceRepresentationSimple {

    private LocalDate availableDate;

    private Integer durationMinimum;

    private Integer durationMaximum;

    public LocalDate getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }

    public Integer getDurationMinimum() {
        return durationMinimum;
    }

    public void setDurationMinimum(Integer durationMinimum) {
        this.durationMinimum = durationMinimum;
    }

    public Integer getDurationMaximum() {
        return durationMaximum;
    }

    public void setDurationMaximum(Integer durationMaximum) {
        this.durationMaximum = durationMaximum;
    }

}
