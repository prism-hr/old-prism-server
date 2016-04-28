package uk.co.alumeni.prism.rest.representation.resource;

import java.util.List;

public class ResourceParentRepresentationSummary extends ResourceRepresentationSummary implements ResourceParentRepresentationClient {

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
