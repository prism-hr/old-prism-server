package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

public interface ResourceRepresentationClient {

    public List<ResourceCountRepresentation> getCounts();

    public void setCounts(List<ResourceCountRepresentation> counts);

    public ResourceSummaryPlotRepresentation getPlot();

    public void setPlot(ResourceSummaryPlotRepresentation plot);

}
