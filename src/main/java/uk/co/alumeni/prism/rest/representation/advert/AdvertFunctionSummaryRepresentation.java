package uk.co.alumeni.prism.rest.representation.advert;

import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;

public class AdvertFunctionSummaryRepresentation extends AdvertCategoryNameEnumRepresentation<PrismAdvertFunction> {

    public AdvertFunctionSummaryRepresentation withId(PrismAdvertFunction id) {
        setId(id);
        return this;
    }

    public AdvertFunctionSummaryRepresentation withAdvertCount(Long advertCount) {
        setAdvertCount(advertCount);
        return this;
    }

}
