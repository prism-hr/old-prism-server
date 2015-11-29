package uk.co.alumeni.prism.rest.representation.advert;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import uk.co.alumeni.prism.rest.representation.ListRepresentation;
import uk.co.alumeni.prism.rest.representation.ListSummaryRepresentation;

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

    public boolean isNotEmpty() {
        return CollectionUtils.isNotEmpty(rows);
    }

}