package uk.co.alumeni.prism.rest.representation.advert;

import static org.apache.commons.lang3.ObjectUtils.compare;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationIdentity;

public class AdvertInstitutionSummaryRepresentation extends AdvertCategorySummaryRepresentation<ResourceRepresentationIdentity> implements
        Comparable<AdvertInstitutionSummaryRepresentation> {

    public AdvertInstitutionSummaryRepresentation withId(ResourceRepresentationIdentity id) {
        setId(id);
        return this;
    }

    public AdvertInstitutionSummaryRepresentation withAdvertCount(Long advertCount) {
        setAdvertCount(advertCount);
        return this;
    }

    @Override
    public int compareTo(AdvertInstitutionSummaryRepresentation other) {
        return compare(getId(), other.getId());
    }

}
