package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.ListRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ListSummaryRepresentation;

public class ResourceListRepresentation extends ListRepresentation<ResourceListRowRepresentation> {

    private List<ResourceListRowRepresentation> rows;

    public List<ResourceListRowRepresentation> getRows() {
        return rows;
    }

    public void setRows(List<ResourceListRowRepresentation> rows) {
        this.rows = rows;
    }

    public ResourceListRepresentation withRows(List<ResourceListRowRepresentation> rows) {
        this.rows = rows;
        return this;
    }

    public ResourceListRepresentation withSummaries(List<ListSummaryRepresentation> summaries) {
        setSummaries(summaries);
        return this;
    }

}
