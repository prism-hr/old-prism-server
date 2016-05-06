package uk.co.alumeni.prism.domain.profile;

public interface ProfileEmploymentPosition<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>> extends ProfileAdvertRelationSection<T> {

    Integer getStartYear();

    void setStartYear(Integer startYear);

    Integer getStartMonth();

    void setStartMonth(Integer startMonth);

    Integer getEndYear();

    void setEndYear(Integer endYear);

    Integer getEndMonth();

    void setEndMonth(Integer endMonth);

    Boolean getCurrent();

    void setCurrent(Boolean current);

}
