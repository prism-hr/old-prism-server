package com.zuehlke.pgadmissions.rest.dto.application;

public class ApplicationAddressDTO {

    private AddressDTO currentAddress;

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