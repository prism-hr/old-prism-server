package com.zuehlke.pgadmissions.dto;

public class ApplicationRatingDTO {

    Long ratingCount;
    
    Double ratingAverage;

    public final Long getRatingCount() {
        return ratingCount;
    }

    public final void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public final Double getRatingAverage() {
        return ratingAverage;
    }

    public final void setRatingAverage(Double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

}
