package uk.co.alumeni.prism.rest.representation.advert;

import static org.apache.commons.lang3.ObjectUtils.compare;

public class AdvertCategoryNameStringSummaryRepresentation extends AdvertCategorySummaryRepresentation<Integer> implements
        Comparable<AdvertCategoryNameStringSummaryRepresentation> {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AdvertCategoryNameStringSummaryRepresentation withId(Integer id) {
        setId(id);
        return this;
    }

    public AdvertCategoryNameStringSummaryRepresentation withName(String name) {
        this.name = name;
        return this;
    }

    public AdvertCategoryNameStringSummaryRepresentation withAdvertCount(Long advertCount) {
        setAdvertCount(advertCount);
        return this;
    }

    @Override
    public int compareTo(AdvertCategoryNameStringSummaryRepresentation other) {
        return compare(name, other.getName());
    }

}
