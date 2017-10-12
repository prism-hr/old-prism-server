package uk.co.alumeni.prism.rest.representation.resource;

import uk.co.alumeni.prism.rest.representation.ListRepresentation;
import uk.co.alumeni.prism.rest.representation.ListSummaryRepresentation;

import java.util.List;

public class ResourceListRepresentation extends ListRepresentation<ResourceListRowRepresentation> {

    private List<ResourceListRowRepresentation> rows;

    private List<ListSummaryRepresentation> urgentSummaries;

    public List<ResourceListRowRepresentation> getRows() {
        return rows;
    }

    public void setRows(List<ResourceListRowRepresentation> rows) {
        this.rows = rows;
    }

    public List<ListSummaryRepresentation> getUrgentSummaries() {
        return urgentSummaries;
    }

    public void setUrgentSummaries(List<ListSummaryRepresentation> urgentSummaries) {
        this.urgentSummaries = urgentSummaries;
    }

    public ResourceListRepresentation withRows(List<ResourceListRowRepresentation> rows) {
        this.rows = rows;
        return this;
    }

    public ResourceListRepresentation withSummaries(List<ListSummaryRepresentation> summaries) {
        setSummaries(summaries);
        return this;
    }

    public ResourceListRepresentation withUrgentSummaries(List<ListSummaryRepresentation> urgentSummaries) {
        setUrgentSummaries(urgentSummaries);
        return this;
    }

}
