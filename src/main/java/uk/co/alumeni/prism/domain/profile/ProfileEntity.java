package uk.co.alumeni.prism.domain.profile;

import java.util.Set;

import uk.co.alumeni.prism.domain.user.User;

public interface ProfileEntity<T extends ProfilePersonalDetail<?>, U extends ProfileAddress<?>, V extends ProfileQualification<?>, W extends ProfileAward<?>, X extends ProfileEmploymentPosition<?>, Y extends ProfileReferee<?>, Z extends ProfileDocument<?>, A extends ProfileAdditionalInformation<?>> {

    Integer getId();

    void setId(Integer id);

    User getUser();

    void setUser(User user);

    T getPersonalDetail();

    void setPersonalDetail(T personalDetail);

    U getAddress();

    void setAddress(U address);

    Set<V> getQualifications();

    Set<W> getAwards();

    Set<X> getEmploymentPositions();

    Set<Y> getReferees();

    Z getDocument();

    void setDocument(Z document);

    A getAdditionalInformation();

    void setAdditionalInformation(A additionalInformation);

    Boolean getShared();

    void setShared(Boolean shared);

}
