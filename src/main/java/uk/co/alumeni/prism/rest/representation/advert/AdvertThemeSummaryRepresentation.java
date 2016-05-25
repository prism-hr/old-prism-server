package uk.co.alumeni.prism.rest.representation.advert;

import static org.apache.commons.lang3.ObjectUtils.compare;

public class AdvertThemeSummaryRepresentation extends AdvertCategorySummaryRepresentation implements Comparable<AdvertThemeSummaryRepresentation> {

    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AdvertThemeSummaryRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public AdvertThemeSummaryRepresentation withName(String name) {
        this.name = name;
        return this;
    }

    public AdvertThemeSummaryRepresentation withAdvertCount(Long advertCount) {
        setAdvertCount(advertCount);
        return this;
    }

    @Override
    public int compareTo(AdvertThemeSummaryRepresentation other) {
        return compare(name, other.getName());
    }

}
