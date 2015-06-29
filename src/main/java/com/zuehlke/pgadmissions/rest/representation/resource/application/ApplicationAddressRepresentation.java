package com.zuehlke.pgadmissions.rest.representation.resource.application;

public class ApplicationAddressRepresentation extends ApplicationSectionRepresentation {

    private ApplicationAddressRepresentationApplication currentAddress;

    private ApplicationAddressRepresentationApplication contactAddress;

    public ApplicationAddressRepresentationApplication getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(ApplicationAddressRepresentationApplication currentAddress) {
        this.currentAddress = currentAddress;
    }

    public ApplicationAddressRepresentationApplication getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(ApplicationAddressRepresentationApplication contactAddress) {
        this.contactAddress = contactAddress;
    }
}
