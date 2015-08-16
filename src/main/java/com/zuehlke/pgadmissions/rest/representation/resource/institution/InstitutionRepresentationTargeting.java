package com.zuehlke.pgadmissions.rest.representation.resource.institution;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationLocation;

import java.math.BigDecimal;

public class InstitutionRepresentationTargeting extends ResourceRepresentationLocation {

    private BigDecimal relevance;

    private BigDecimal distance;

    public BigDecimal getRelevance() {
        return relevance;
    }

    public void setRelevance(BigDecimal relevance) {
        this.relevance = relevance;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

}
