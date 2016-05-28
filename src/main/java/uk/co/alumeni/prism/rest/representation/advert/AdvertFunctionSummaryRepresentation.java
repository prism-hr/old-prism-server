package uk.co.alumeni.prism.rest.representation.advert;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang3.ObjectUtils.compare;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;

import com.google.common.base.Objects;

public class AdvertFunctionSummaryRepresentation extends AdvertCategorySummaryRepresentation implements Comparable<AdvertFunctionSummaryRepresentation> {

    private PrismAdvertFunction function;

    public PrismAdvertFunction getFunction() {
        return function;
    }

    public void setFunction(PrismAdvertFunction function) {
        this.function = function;
    }

    public AdvertFunctionSummaryRepresentation withFunction(PrismAdvertFunction function) {
        this.function = function;
        return this;
    }

    public AdvertFunctionSummaryRepresentation withAdvertCount(Long advertCount) {
        setAdvertCount(advertCount);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(function);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        return equal(function, ((AdvertFunctionSummaryRepresentation) object).getFunction());
    }

    @Override
    public int compareTo(AdvertFunctionSummaryRepresentation other) {
        return compare(function.name(), other.getFunction().name());
    }

}
