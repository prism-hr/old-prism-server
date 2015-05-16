package com.zuehlke.pgadmissions.rest.representation;

import java.util.Set;

import com.google.common.collect.Sets;

public class ResourceSummaryPlotsRepresentation {

    private Set<ResourceSummaryPlotRepresentation> plots = Sets.newHashSet();

    public Set<ResourceSummaryPlotRepresentation> getPlots() {
        return plots;
    }

    public ResourceSummaryPlotsRepresentation addPlot(ResourceSummaryPlotRepresentation plot) {
        plots.add(plot);
        return this;
    }

}
