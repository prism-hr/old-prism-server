package com.zuehlke.pgadmissions.rest.representation.resource.application;

public class ApplicationAddressRepresentation extends ApplicationSectionRepresentation {

    private AddressRepresentationApplication currentAddress;

    private AddressRepresentationApplication contactAddress;

    public AddressRepresentationApplication getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(AddressRepresentationApplication currentAddress) {
        this.currentAddress = currentAddress;
    }

    public AddressRepresentationApplication getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(AddressRepresentationApplication contactAddress) {
        this.contactAddress = contactAddress;
    }
}
