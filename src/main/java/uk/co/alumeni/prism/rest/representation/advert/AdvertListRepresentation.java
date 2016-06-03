package uk.co.alumeni.prism.rest.representation.advert;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.alumeni.prism.rest.representation.ListRepresentation;
import uk.co.alumeni.prism.rest.representation.ListSummaryRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationOccurrence;

public class AdvertListRepresentation extends ListRepresentation<AdvertRepresentationExtended> {

    private List<AdvertRepresentationExtended> rows;

    private Integer invisibleAdvertCount;

    private Set<ResourceRepresentationOccurrence> invisibleAdvertInstitutions;

    private Map<String, List<? extends AdvertCategorySummaryRepresentation>> filterValues;

    public List<AdvertRepresentationExtended> getRows() {
        return rows;
    }

    public void setRows(List<AdvertRepresentationExtended> rows) {
        this.rows = rows;
    }

    public Integer getInvisibleAdvertCount() {
        return invisibleAdvertCount;
    }

    public void setInvisibleAdvertCount(Integer invisibleAdvertCount) {
        this.invisibleAdvertCount = invisibleAdvertCount;
    }

    public Set<ResourceRepresentationOccurrence> getInvisibleAdvertInstitutions() {
        return invisibleAdvertInstitutions;
    }

    public void setInvisibleAdvertInstitutions(Set<ResourceRepresentationOccurrence> invisibleAdvertInstitutions) {
        this.invisibleAdvertInstitutions = invisibleAdvertInstitutions;
    }

    public Map<String, List<? extends AdvertCategorySummaryRepresentation>> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(Map<String, List<? extends AdvertCategorySummaryRepresentation>> filterValues) {
        this.filterValues = filterValues;
    }

    public AdvertListRepresentation withRows(List<AdvertRepresentationExtended> rows) {
        this.rows = rows;
        return this;
    }

    public AdvertListRepresentation withInvisibleAdvertCount(Integer invisibleAdvertCount) {
        this.invisibleAdvertCount = invisibleAdvertCount;
        return this;
    }

    public AdvertListRepresentation withInvisibleAdvertInstitutions(Set<ResourceRepresentationOccurrence> invisibleAdvertInstitutions) {
        this.invisibleAdvertInstitutions = invisibleAdvertInstitutions;
        return this;
    }

    public AdvertListRepresentation withSummaries(List<ListSummaryRepresentation> summaries) {
        setSummaries(summaries);
        return this;
    }

    public AdvertListRepresentation withFilterValues(Map<String, List<? extends AdvertCategorySummaryRepresentation>> filterProperties) {
        this.filterValues = filterProperties;
        return this;
    }

    public boolean hasNotifiableUpdates() {
        return isNotEmpty(rows);
    }

}
