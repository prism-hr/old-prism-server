package com.zuehlke.pgadmissions.domain.profile;

public interface ProfileSection<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> {

    Integer getId();

    void setId(Integer id);

    T getAssociation();

    void setAssociation(T association);

}
