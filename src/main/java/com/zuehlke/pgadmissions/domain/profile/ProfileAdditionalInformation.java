package com.zuehlke.pgadmissions.domain.profile;

public interface ProfileAdditionalInformation<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    String getConvictionsText();

    void setConvictionsText(String convictionsText);

}
