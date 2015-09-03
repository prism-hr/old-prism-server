package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

public class ResourceSummaryPlotRepresentation {

    private List<ResourceSummaryPlotConstraintRepresentation> constraints;

    private ResourceSummaryPlotDataRepresentation data;

    public List<ResourceSummaryPlotConstraintRepresentation> getConstraints() {
        return constraints;
    }

    public ResourceSummaryPlotDataRepresentation getData() {
        return data;
    }

    public ResourceSummaryPlotRepresentation withConstraints(List<ResourceSummaryPlotConstraintRepresentation> constraints) {
        this.constraints = constraints;
        return this;
    }

    public ResourceSummaryPlotRepresentation withData(ResourceSummaryPlotDataRepresentation data) {
        this.data = data;
        return this;
    }

}
