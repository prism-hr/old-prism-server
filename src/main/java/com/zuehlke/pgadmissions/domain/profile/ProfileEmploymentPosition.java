package com.zuehlke.pgadmissions.domain.profile;

public interface ProfileEmploymentPosition<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> extends ProfileAdvertRelationSection<T> {

    Integer getStartYear();

    void setStartYear(Integer startYear);

    Integer getStartMonth();

    void setStartMonth(Integer startMonth);

    Integer getEndYear();

    void setEndYear(Integer endYear);

    Integer getEndMonth();

    void setEndMonth(Integer endMonth);

    String getDescription();

    void setDescription(String description);

    Boolean getCurrent();

    void setCurrent(Boolean current);

}
