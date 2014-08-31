package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

public class ApplicationRatingDTO {

    Integer ratingCount;
    
    BigDecimal ratingAverage;

    public final Integer getRatingCount() {
        return ratingCount;
    }

    public final void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public final BigDecimal getRatingAverage() {
        return ratingAverage;
    }

    public final void setRatingAverage(BigDecimal ratingAverage) {
        this.ratingAverage = ratingAverage;
    }
    
}
