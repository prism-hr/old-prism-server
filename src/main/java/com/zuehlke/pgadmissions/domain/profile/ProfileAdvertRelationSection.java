package com.zuehlke.pgadmissions.domain.profile;

import com.zuehlke.pgadmissions.domain.advert.Advert;

public interface ProfileAdvertRelationSection<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    Advert getAdvert();

    void setAdvert(Advert advert);

}
