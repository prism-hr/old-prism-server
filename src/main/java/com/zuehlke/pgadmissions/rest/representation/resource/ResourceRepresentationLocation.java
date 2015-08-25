package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.address.AddressAdvertRepresentation;

public class ResourceRepresentationLocation extends ResourceRepresentationSimple {

    private AddressAdvertRepresentation address;

    public AddressAdvertRepresentation getAddress() {
        return address;
    }

    public void setAddress(AddressAdvertRepresentation address) {
        this.address = address;
    }

}
