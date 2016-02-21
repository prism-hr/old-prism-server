package uk.co.alumeni.prism.rest.representation.resource;

import java.util.List;

public interface ResourceRepresentationClient {

    List<ResourceCountRepresentation> getCounts();

    void setCounts(List<ResourceCountRepresentation> counts);

    ResourceSummaryPlotRepresentation getPlot();

    void setPlot(ResourceSummaryPlotRepresentation plot);

}
