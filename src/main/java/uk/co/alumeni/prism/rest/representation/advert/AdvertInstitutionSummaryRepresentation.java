package uk.co.alumeni.prism.rest.representation.advert;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationIdentity;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang3.ObjectUtils.compare;

public class AdvertInstitutionSummaryRepresentation extends AdvertCategorySummaryRepresentation implements Comparable<AdvertInstitutionSummaryRepresentation> {

    public ResourceRepresentationIdentity institution;

    public ResourceRepresentationIdentity getInstitution() {
        return institution;
    }

    public void setInstitution(ResourceRepresentationIdentity institution) {
        this.institution = institution;
    }

    public AdvertInstitutionSummaryRepresentation withInstitution(ResourceRepresentationIdentity institution) {
        this.institution = institution;
        return this;
    }

    public AdvertInstitutionSummaryRepresentation withAdvertCount(Long advertCount) {
        setAdvertCount(advertCount);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(institution);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        return equal(institution, ((AdvertInstitutionSummaryRepresentation) object).getInstitution());
    }

    @Override
    public int compareTo(AdvertInstitutionSummaryRepresentation other) {
        return compare(institution, other.getInstitution());
    }

}
