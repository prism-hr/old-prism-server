package com.zuehlke.pgadmissions.domain.profile;

import java.util.Set;

import com.zuehlke.pgadmissions.domain.user.User;

public interface ProfileEntity<T extends ProfilePersonalDetail<?>, U extends ProfileAddress<?>, V extends ProfileQualification<?>, W extends ProfileEmploymentPosition<?>, X extends ProfileReferee<?>, Y extends ProfileDocument<?>, Z extends ProfileAdditionalInformation<?>> {

    Integer getId();

    void setId(Integer id);

    User getUser();

    void setUser(User user);

    T getPersonalDetail();

    void setPersonalDetail(T personalDetail);

    U getAddress();

    void setAddress(U address);

    Set<V> getQualifications();

    Set<W> getEmploymentPositions();

    Set<X> getReferees();

    Y getDocument();

    void setDocument(Y document);

    Z getAdditionalInformation();

    void setAdditionalInformation(Z additionalInformation);
    
    Boolean getShared();
    
    void setShared(Boolean shared);

}
