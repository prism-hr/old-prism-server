package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

public class ApplicationRatingSummaryDTO {
    
    private ResourceParent parent;

    private Long applicationRatingCount;

    private Long applicationRatingApplications;

    private Double applicationRatingAverage;
    
    public ResourceParent getParent() {
        return parent;
    }

    public void setParent(ResourceParent parent) {
        this.parent = parent;
    }

    public Long getApplicationRatingCount() {
        return applicationRatingCount;
    }

    public void setApplicationRatingCount(Long applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
    }

    public Long getApplicationRatingApplications() {
        return applicationRatingApplications;
    }

    public void setApplicationRatingApplications(Long applicationRatingApplications) {
        this.applicationRatingApplications = applicationRatingApplications;
    }

    public Double getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(Double applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

}
