package uk.co.alumeni.prism.domain.profile;

import uk.co.alumeni.prism.domain.user.User;

public interface ProfileReferee<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>> extends ProfileAdvertRelationSection<T> {

    String getSkype();

    void setSkype(String skype);

    User getUser();

    void setUser(User user);

    String getPhone();

    void setPhone(String phone);

}
