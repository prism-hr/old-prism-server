package uk.co.alumeni.prism.rest.representation.advert;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang3.ObjectUtils.compare;

public class AdvertIndustrySummaryRepresentation extends AdvertCategorySummaryRepresentation implements Comparable<AdvertIndustrySummaryRepresentation> {

    private PrismAdvertIndustry industry;

    public PrismAdvertIndustry getIndustry() {
        return industry;
    }

    public void setIndustry(PrismAdvertIndustry industry) {
        this.industry = industry;
    }

    public AdvertIndustrySummaryRepresentation withIndustry(PrismAdvertIndustry industry) {
        this.industry = industry;
        return this;
    }

    public AdvertIndustrySummaryRepresentation withAdvertCount(Long advertCount) {
        setAdvertCount(advertCount);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(industry);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        return equal(industry, ((AdvertIndustrySummaryRepresentation) object).getIndustry());
    }

    @Override
    public int compareTo(AdvertIndustrySummaryRepresentation other) {
        return compare(industry.name(), other.getIndustry().name());
    }

}
