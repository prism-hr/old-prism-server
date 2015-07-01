package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.rest.representation.address.AddressApplicationRepresentation;

public class ApplicationAddressRepresentation extends ApplicationSectionRepresentation {

    private AddressApplicationRepresentation currentAddress;

    private AddressApplicationRepresentation contactAddress;

    public AddressApplicationRepresentation getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(AddressApplicationRepresentation currentAddress) {
        this.currentAddress = currentAddress;
    }

    public AddressApplicationRepresentation getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(AddressApplicationRepresentation contactAddress) {
        this.contactAddress = contactAddress;
    }

    public ApplicationAddressRepresentation withCurrentAddress(AddressApplicationRepresentation currentAddress) {
        this.currentAddress = currentAddress;
        return this;
    }

    public ApplicationAddressRepresentation withContactAddress(AddressApplicationRepresentation contactAddress) {
        this.contactAddress = contactAddress;
        return this;
    }

}
