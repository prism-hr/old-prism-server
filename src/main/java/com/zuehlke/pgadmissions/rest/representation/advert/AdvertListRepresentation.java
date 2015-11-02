package com.zuehlke.pgadmissions.rest.representation.advert;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.ListRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ListSummaryRepresentation;

public class AdvertListRepresentation extends ListRepresentation<AdvertRepresentationExtended> {

    private List<AdvertRepresentationExtended> rows;

    public List<AdvertRepresentationExtended> getRows() {
        return rows;
    }

    public void setRows(List<AdvertRepresentationExtended> rows) {
        this.rows = rows;
    }

    public AdvertListRepresentation withRows(List<AdvertRepresentationExtended> rows) {
        this.rows = rows;
        return this;
    }

    public AdvertListRepresentation withSummaries(List<ListSummaryRepresentation> summaries) {
        setSummaries(summaries);
        return this;
    }

}
