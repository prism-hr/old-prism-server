package com.zuehlke.pgadmissions.dto.resource;

import java.math.BigDecimal;

public class ResourceTargetRelevanceDTO {

    private Integer resourceId;
    
    private BigDecimal targetingRelevance;

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public BigDecimal getTargetingRelevance() {
        return targetingRelevance;
    }

    public void setTargetingRelevance(BigDecimal targetingRelevance) {
        this.targetingRelevance = targetingRelevance;
    }
    
}
