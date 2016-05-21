package uk.co.alumeni.prism.dto;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang3.ObjectUtils.compare;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;

import com.google.common.base.Objects;

public class AdvertIndustrySummaryDTO implements Comparable<AdvertIndustrySummaryDTO> {

    private PrismAdvertIndustry industry;

    private Long advertCount;

    public PrismAdvertIndustry getIndustry() {
        return industry;
    }

    public void setIndustry(PrismAdvertIndustry industry) {
        this.industry = industry;
    }

    public Long getAdvertCount() {
        return advertCount;
    }

    public void setAdvertCount(Long advertCount) {
        this.advertCount = advertCount;
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
        return equal(industry, ((AdvertIndustrySummaryDTO) object).getIndustry());
    }

    @Override
    public int compareTo(AdvertIndustrySummaryDTO other) {
        return compare(industry.name(), other.getIndustry().name());
    }

}
