package uk.co.alumeni.prism.domain.profile;

public interface ProfileAward<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Integer getAwardYear();

    void setAwardYear(Integer awardYear);

    Integer getAwardMonth();

    void setAwardMonth(Integer awardMonth);

}
