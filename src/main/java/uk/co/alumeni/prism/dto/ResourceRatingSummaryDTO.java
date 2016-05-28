package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.resource.Resource;

public class ResourceRatingSummaryDTO {

    private Resource resource;

    private Long resourceCount;
    
    private Long ratingCount;

    private Double ratingAverage;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Long getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(Long resourceCount) {
        this.resourceCount = resourceCount;
    }
    
    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Double getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(Double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

}
