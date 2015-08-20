package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

public class ResourceSummaryPlotConstraintRepresentation {

    private Integer entityId;

    private PrismImportedEntity entityType;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer id) {
        this.entityId = id;
    }

    public PrismImportedEntity getEntityType() {
        return entityType;
    }

    public void setEntityType(PrismImportedEntity entityType) {
        this.entityType = entityType;
    }
    
    public ResourceSummaryPlotConstraintRepresentation withEntityId(Integer entityId) {
        this.entityId = entityId;
        return this;
    }
    
    public ResourceSummaryPlotConstraintRepresentation withType(PrismImportedEntity entityType) {
        this.entityType = entityType;
        return this;
    }

}
