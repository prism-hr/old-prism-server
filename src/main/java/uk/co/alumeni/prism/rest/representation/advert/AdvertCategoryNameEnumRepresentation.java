package uk.co.alumeni.prism.rest.representation.advert;

import static org.apache.commons.lang3.ObjectUtils.compare;

public class AdvertCategoryNameEnumRepresentation<T extends Enum<T>> extends AdvertCategorySummaryRepresentation<T> implements
        Comparable<AdvertCategoryNameEnumRepresentation<T>> {

    @Override
    public int compareTo(AdvertCategoryNameEnumRepresentation<T> other) {
        return compare(getId().name(), other.getId().name());
    }

}
