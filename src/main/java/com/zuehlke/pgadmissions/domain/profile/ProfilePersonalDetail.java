package com.zuehlke.pgadmissions.domain.profile;

import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

public interface ProfilePersonalDetail<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    ImportedEntitySimple getTitle();

    void setTitle(ImportedEntitySimple title);

    ImportedEntitySimple getGender();

    void setGender(ImportedEntitySimple gender);

    ImportedDomicile getDomicile();

    void setDomicile(ImportedDomicile domicile);

    ImportedDomicile getNationality();

    void setNationality(ImportedDomicile nationality);

    Boolean getVisaRequired();

    void setVisaRequired(Boolean visaRequired);

    String getPhone();

    void setPhone(String phone);

    String getSkype();

    void setSkype(String skype);

    ImportedEntitySimple getEthnicity();

    void setEthnicity(ImportedEntitySimple ethnicity);

    ImportedEntitySimple getDisability();

    void setDisability(ImportedEntitySimple disability);

}
