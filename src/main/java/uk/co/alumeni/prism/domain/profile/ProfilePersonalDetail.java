package uk.co.alumeni.prism.domain.profile;

import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.definitions.PrismDisability;
import uk.co.alumeni.prism.domain.definitions.PrismEthnicity;
import uk.co.alumeni.prism.domain.definitions.PrismGender;

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
    
    PrismEthnicity getEthnicity();
    
    void setEthnicity(PrismEthnicity ethnicity);
    
    PrismDisability getDisability();
    
    void setDisability(PrismDisability disability);

}
