package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

public class ApplicationStartDateRepresentation {

    private LocalDate earliestDate;
    
    private LocalDate recommendedDate;
    
    private LocalDate latestDate;

    public final LocalDate getEarliestDate() {
        return earliestDate;
    }

    public final void setEarliestDate(LocalDate earliestDate) {
        this.earliestDate = earliestDate;
    }

    public final LocalDate getRecommendedDate() {
        return recommendedDate;
    }

    public final void setRecommendedDate(LocalDate recommendedDate) {
        this.recommendedDate = recommendedDate;
    }

    public final LocalDate getLatestDate() {
        return latestDate;
    }

    public final void setLatestDate(LocalDate latestDate) {
        this.latestDate = latestDate;
    }
    
    public ApplicationStartDateRepresentation withEarliestDate(LocalDate earliestDate) {
        this.earliestDate = earliestDate;
        return this;
    }
    
    public ApplicationStartDateRepresentation withRecommendedDate(LocalDate recommendedDate) {
        this.recommendedDate = recommendedDate;
        return this;
    }
    
    public ApplicationStartDateRepresentation withLatestDate(LocalDate latestDate) {
        this.latestDate = latestDate;
        return this;
    }
    
}
