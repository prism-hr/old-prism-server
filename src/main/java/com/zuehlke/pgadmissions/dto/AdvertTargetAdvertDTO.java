package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

public class AdvertTargetAdvertDTO {
    
    private Integer targetAdvertId;

    private Integer ratingCount;

    private BigDecimal ratingAverage;
    
    public Integer getTargetAdvertId() {
        return targetAdvertId;
    }

    public void setTargetAdvertId(Integer targetAdvertId) {
        this.targetAdvertId = targetAdvertId;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public BigDecimal getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(BigDecimal ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

}
