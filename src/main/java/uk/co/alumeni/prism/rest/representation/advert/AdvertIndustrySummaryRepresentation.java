package uk.co.alumeni.prism.rest.representation.advert;

import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;

public class AdvertIndustrySummaryRepresentation extends AdvertCategoryNameEnumRepresentation<PrismAdvertIndustry> {

    public AdvertIndustrySummaryRepresentation withId(PrismAdvertIndustry id) {
        setId(id);
        return this;
    }

    public AdvertIndustrySummaryRepresentation withAdvertCount(Long advertCount) {
        setAdvertCount(advertCount);
        return this;
    }

}
