package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public class ApplicationRatingSummaryDTO {

    private Resource resource;

    private Long applicationRatingCount;

    private Long applicationRatingApplications;

    private Double applicationRatingAverage;

    public Resource getResource() {
        return resource;
    }

    public void setParent(Resource resoure) {
        this.resource = resoure;
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

    public ApplicationRatingSummaryDTO withInitialValues(Resource resource, Double applicationRatingAverage) {
        this.resource = resource;
        this.applicationRatingCount = new Long(1);
        this.applicationRatingApplications = new Long(1);
        this.applicationRatingAverage = applicationRatingAverage;
        return this;
    }

}
