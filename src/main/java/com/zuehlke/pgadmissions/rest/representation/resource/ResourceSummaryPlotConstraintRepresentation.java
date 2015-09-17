package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismFilterEntity;

public class ResourceSummaryPlotConstraintRepresentation {

    private Integer entityId;

    private PrismFilterEntity entityType;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer id) {
        this.entityId = id;
    }

    public PrismFilterEntity getEntityType() {
        return entityType;
    }

    public void setEntityType(PrismFilterEntity entityType) {
        this.entityType = entityType;
    }

    public ResourceSummaryPlotConstraintRepresentation withEntityId(Integer entityId) {
        this.entityId = entityId;
        return this;
    }

    public ResourceSummaryPlotConstraintRepresentation withType(PrismFilterEntity entityType) {
        this.entityType = entityType;
        return this;
    }

}
