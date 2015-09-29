package com.zuehlke.pgadmissions.rest.dto.profile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.rest.dto.AddressDTO;

public class ProfileAddressDTO {

    @NotNull
    @Valid
    private AddressDTO currentAddress;

    @NotNull
    @Valid
    private AddressDTO contactAddress;

    public AddressDTO getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(AddressDTO currentAddress) {
        this.currentAddress = currentAddress;
    }

    public AddressDTO getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(AddressDTO contactAddress) {
        this.contactAddress = contactAddress;
    }
}