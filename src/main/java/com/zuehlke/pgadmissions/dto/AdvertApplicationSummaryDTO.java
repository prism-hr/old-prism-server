package com.zuehlke.pgadmissions.dto;

public class AdvertApplicationSummaryDTO {

    private Long applicationCount;

    private Long applicationRatingCount;

    private Double applicationRatingAverage;

    public Long getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Long applicationCount) {
        this.applicationCount = applicationCount;
    }

    public Long getApplicationRatingCount() {
        return applicationRatingCount;
    }

    public void setApplicationRatingCount(Long applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
    }

    public Double getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(Double applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

}
