package uk.co.alumeni.prism.rest.representation.profile;

import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationSectionRepresentation;

public class ProfileAddressRepresentation extends ApplicationSectionRepresentation {

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

    public ProfileAddressRepresentation withCurrentAddress(AddressRepresentation currentAddress) {
        this.currentAddress = currentAddress;
        return this;
    }

    public ProfileAddressRepresentation withContactAddress(AddressRepresentation contactAddress) {
        this.contactAddress = contactAddress;
        return this;
    }

}
