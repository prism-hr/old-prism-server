package com.zuehlke.pgadmissions.dto;

public class ApplicationRatingSummaryDTO {

    Double ratingCountAverage;
    
    Double ratingAverage;

    public final Double getRatingCountAverage() {
        return ratingCountAverage;
    }

    public final void setRatingCountAverage(Double ratingCountAverage) {
        this.ratingCountAverage = ratingCountAverage;
    }

    public final Double getRatingAverage() {
        return ratingAverage;
    }

    public final void setRatingAverage(Double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }
    
}
