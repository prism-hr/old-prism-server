package com.zuehlke.pgadmissions.dto.resource;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public class ResourceRatingSummaryDTO {

    private Resource resource;

    private Long ratingCount;

    private Long ratingResources;

    private Double ratingAverage;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Long getRatingResources() {
        return ratingResources;
    }

    public void setRatingResources(Long ratingApplications) {
        this.ratingResources = ratingApplications;
    }

    public Double getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(Double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

}
