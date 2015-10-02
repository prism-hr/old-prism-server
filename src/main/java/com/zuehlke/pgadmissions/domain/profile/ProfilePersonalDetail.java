package com.zuehlke.pgadmissions.domain.profile;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.definitions.PrismGender;

public interface ProfilePersonalDetail<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    PrismGender getGender();

    void setGender(PrismGender gender);

    Domicile getDomicile();

    void setDomicile(Domicile domicile);

    Domicile getNationality();

    void setNationality(Domicile nationality);

    Boolean getVisaRequired();

    void setVisaRequired(Boolean visaRequired);

    String getPhone();

    void setPhone(String phone);

    String getSkype();

    void setSkype(String skype);

}
