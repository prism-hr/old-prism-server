package com.zuehlke.pgadmissions.rest.representation.resource.institution;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceCountRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotRepresentation;

public class InstitutionRepresentationClient extends InstitutionRepresentation implements ResourceRepresentationClient {

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
