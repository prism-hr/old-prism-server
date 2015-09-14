package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;

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

    public ApplicationAddressRepresentation withCurrentAddress(AddressRepresentation currentAddress) {
        this.currentAddress = currentAddress;
        return this;
    }

    public ApplicationAddressRepresentation withContactAddress(AddressRepresentation contactAddress) {
        this.contactAddress = contactAddress;
        return this;
    }
    
    public ApplicationAddressRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }
    

}
