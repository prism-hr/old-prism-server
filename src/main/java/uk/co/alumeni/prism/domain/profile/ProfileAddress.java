package uk.co.alumeni.prism.domain.profile;

import uk.co.alumeni.prism.domain.address.Address;

public interface ProfileAddress<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    Address getCurrentAddress();

    void setCurrentAddress(Address currentAddress);

    Address getContactAddress();

    void setContactAddress(Address contactAddress);

}
