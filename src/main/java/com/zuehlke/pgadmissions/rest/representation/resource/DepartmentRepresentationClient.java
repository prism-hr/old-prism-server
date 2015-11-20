package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

public class DepartmentRepresentationClient extends ResourceParentRepresentation implements ResourceRepresentationClient {

    private List<ResourceCountRepresentation> counts;

    private ResourceSummaryPlotRepresentation plot;

    @Override
    public List<ResourceCountRepresentation> getCounts() {
        return counts;
    }

    @Override
    public void setCounts(List<ResourceCountRepresentation> counts) {
        this.counts = counts;
    }

    @Override
    public ResourceSummaryPlotRepresentation getPlot() {
        return plot;
    }

    @Override
    public void setPlot(ResourceSummaryPlotRepresentation plot) {
        this.plot = plot;
    }

}
