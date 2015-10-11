package com.zuehlke.pgadmissions.domain.profile;

import com.zuehlke.pgadmissions.domain.address.Address;

public interface ProfileAddress<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    Address getCurrentAddress();

    void setCurrentAddress(Address currentAddress);

    Address getContactAddress();

    void setContactAddress(Address contactAddress);

}
