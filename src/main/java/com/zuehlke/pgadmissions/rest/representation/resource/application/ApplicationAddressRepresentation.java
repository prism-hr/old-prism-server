package com.zuehlke.pgadmissions.rest.representation.resource.application;

public class ApplicationAddressRepresentation extends ApplicationSectionRepresentation {

    private AddressRepresentation currentAddress;

    private AddressRepresentation contactAddress;

    public AddressRepresentation getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(AddressRepresentation currentAddress) {
        this.currentAddress = currentAddress;
    }

    public AddressRepresentation getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(AddressRepresentation contactAddress) {
        this.contactAddress = contactAddress;
    }
}
