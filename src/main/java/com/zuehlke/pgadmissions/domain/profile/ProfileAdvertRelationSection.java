package com.zuehlke.pgadmissions.domain.profile;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.user.User;

public interface ProfileAdvertRelationSection<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    User getUser();

    void setUser(User user);

    Advert getAdvert();

    void setAdvert(Advert advert);

}
