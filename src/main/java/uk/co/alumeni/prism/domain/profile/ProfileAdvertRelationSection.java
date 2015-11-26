package uk.co.alumeni.prism.domain.profile;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.user.User;

public interface ProfileAdvertRelationSection<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    User getUser();

    void setUser(User user);

    Advert getAdvert();

    void setAdvert(Advert advert);

}
