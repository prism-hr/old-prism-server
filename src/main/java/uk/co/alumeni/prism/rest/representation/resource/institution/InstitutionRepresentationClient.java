package uk.co.alumeni.prism.rest.representation.resource.institution;

import uk.co.alumeni.prism.rest.representation.resource.ResourceCountRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceParentRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.ResourceSummaryPlotRepresentation;

import java.util.List;

public class InstitutionRepresentationClient extends InstitutionRepresentation implements ResourceParentRepresentationClient {

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
