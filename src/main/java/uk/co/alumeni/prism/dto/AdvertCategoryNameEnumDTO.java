package uk.co.alumeni.prism.dto;

import org.apache.commons.lang3.ObjectUtils;

public class AdvertCategoryNameEnumDTO<T extends Enum<T>> extends AdvertCategorySummaryDTO<T> implements Comparable<AdvertCategoryNameEnumDTO<T>> {

    @Override
    public int compareTo(AdvertCategoryNameEnumDTO<T> other) {
        return ObjectUtils.compare(getId().name(), other.getId().name());
    }

}
