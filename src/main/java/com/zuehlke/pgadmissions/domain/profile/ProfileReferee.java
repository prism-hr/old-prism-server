package com.zuehlke.pgadmissions.domain.profile;

import com.zuehlke.pgadmissions.domain.user.User;

public interface ProfileReferee<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> extends ProfileAdvertRelationSection<T> {

    String getSkype();

    void setSkype(String skype);

    User getUser();

    void setUser(User user);

    String getPhone();

    void setPhone(String phone);

}
