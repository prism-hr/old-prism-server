package uk.co.alumeni.prism.rest.representation.resource;

import uk.co.alumeni.prism.domain.definitions.PrismFilterEntity;

public class ResourceSummaryPlotConstraintRepresentation {

    private PrismFilterEntity entityType;

    private String entityId;

    public PrismFilterEntity getEntityType() {
        return entityType;
    }

    public void setEntityType(PrismFilterEntity entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public ResourceSummaryPlotConstraintRepresentation withType(PrismFilterEntity entityType) {
        this.entityType = entityType;
        return this;
    }

    public ResourceSummaryPlotConstraintRepresentation withEntityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

}
