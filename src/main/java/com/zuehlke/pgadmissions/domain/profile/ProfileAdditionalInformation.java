package com.zuehlke.pgadmissions.domain.profile;

public interface ProfileAdditionalInformation<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    String getRequirements();

    void setRequirements(String requirements);

    String getConvictions();

    void setConvictions(String requirements);

}
