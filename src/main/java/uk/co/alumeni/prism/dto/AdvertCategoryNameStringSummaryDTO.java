package uk.co.alumeni.prism.dto;

import static org.apache.commons.lang3.ObjectUtils.compare;

public class AdvertCategoryNameStringSummaryDTO extends AdvertCategorySummaryDTO<Integer> implements Comparable<AdvertCategoryNameStringSummaryDTO> {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(AdvertCategoryNameStringSummaryDTO other) {
        return compare(name, other.getName());
    }

}
