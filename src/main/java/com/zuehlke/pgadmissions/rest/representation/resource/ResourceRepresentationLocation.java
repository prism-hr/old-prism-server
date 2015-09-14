package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;

public class ResourceRepresentationLocation extends ResourceRepresentationSimple {

    private AddressRepresentation address;

    public AddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(AddressRepresentation address) {
        this.address = address;
    }

}
