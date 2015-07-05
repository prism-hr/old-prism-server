package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ApplicationAddressDTO {

    @NotNull
    @Valid
    private AddressApplicationDTO currentAddress;

    @NotNull
    @Valid
    private AddressApplicationDTO contactAddress;

    public AddressApplicationDTO getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(AddressApplicationDTO currentAddress) {
        this.currentAddress = currentAddress;
    }

    public AddressApplicationDTO getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(AddressApplicationDTO contactAddress) {
        this.contactAddress = contactAddress;
    }
}