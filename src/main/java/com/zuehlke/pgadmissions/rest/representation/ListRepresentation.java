package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;

public abstract class ListRepresentation<T> {

    private List<ListSummaryRepresentation> summaries;

    public abstract List<T> getRows();

    public abstract void setRows(List<T> rows);

    public List<ListSummaryRepresentation> getSummaries() {
        return summaries;
    }

    public void setSummaries(List<ListSummaryRepresentation> summaries) {
        this.summaries = summaries;
    }

}
