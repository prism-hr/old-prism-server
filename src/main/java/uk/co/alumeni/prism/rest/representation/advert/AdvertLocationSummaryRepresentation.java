package uk.co.alumeni.prism.rest.representation.advert;

import java.util.List;

public class AdvertLocationSummaryRepresentation extends AdvertCategoryNameStringSummaryRepresentation {

    private List<AdvertLocationSummaryRepresentation> subParts;

    public List<AdvertLocationSummaryRepresentation> getSubParts() {
        return subParts;
    }

    public void setSubParts(List<AdvertLocationSummaryRepresentation> subParts) {
        this.subParts = subParts;
    }

    public AdvertLocationSummaryRepresentation withId(Integer id) {
        setId(id);
        return this;
    }

    public AdvertLocationSummaryRepresentation withName(String name) {
        setName(name);
        return this;
    }

    public AdvertLocationSummaryRepresentation withAdvertCount(Long advertCount) {
        setAdvertCount(advertCount);
        return this;
    }

}
