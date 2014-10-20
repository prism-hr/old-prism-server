package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

public class ApplicationRecommendedStartDateRepresentation {

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
    
    public ApplicationRecommendedStartDateRepresentation withEarliestDate(LocalDate earliestDate) {
        this.earliestDate = earliestDate;
        return this;
    }
    
    public ApplicationRecommendedStartDateRepresentation withRecommendedDate(LocalDate recommendedDate) {
        this.recommendedDate = recommendedDate;
        return this;
    }
    
    public ApplicationRecommendedStartDateRepresentation withLatestDate(LocalDate latestDate) {
        this.latestDate = latestDate;
        return this;
    }
    
}
