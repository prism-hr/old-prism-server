package uk.co.alumeni.prism.rest.representation.advert;

import java.util.List;

public class AdvertLocationSummaryRepresentation extends AdvertThemeSummaryRepresentation {

    private List<AdvertLocationSummaryRepresentation> childLocations;

    public List<AdvertLocationSummaryRepresentation> getChildLocations() {
        return childLocations;
    }

    public void setChildLocations(List<AdvertLocationSummaryRepresentation> childLocations) {
        this.childLocations = childLocations;
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
