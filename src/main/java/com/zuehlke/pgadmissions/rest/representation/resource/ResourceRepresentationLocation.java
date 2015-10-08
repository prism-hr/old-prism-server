package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentation;

public class ResourceRepresentationLocation extends ResourceRepresentationCreation {

    private AddressRepresentation address;

    private UserRepresentation user;

    public AddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(AddressRepresentation address) {
        this.address = address;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }
}
