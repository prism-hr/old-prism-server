package uk.co.alumeni.prism.dto;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang3.ObjectUtils.compare;

public class AdvertFunctionSummaryDTO extends AdvertCategorySummaryDTO implements Comparable<AdvertFunctionSummaryDTO> {

    private PrismAdvertFunction function;

    public PrismAdvertFunction getFunction() {
        return function;
    }

    public void setFunction(PrismAdvertFunction function) {
        this.function = function;
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
        return equal(function, ((AdvertFunctionSummaryDTO) object).getFunction());
    }

    @Override
    public int compareTo(AdvertFunctionSummaryDTO other) {
        return compare(function.name(), other.getFunction().name());
    }

}
