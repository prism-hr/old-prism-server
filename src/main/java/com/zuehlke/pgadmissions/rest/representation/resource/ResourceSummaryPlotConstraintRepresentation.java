package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

public class ResourceSummaryPlotConstraintRepresentation {

    private Integer Id;

    private PrismImportedEntity type;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public PrismImportedEntity getType() {
        return type;
    }

    public void setType(PrismImportedEntity type) {
        this.type = type;
    }

}
