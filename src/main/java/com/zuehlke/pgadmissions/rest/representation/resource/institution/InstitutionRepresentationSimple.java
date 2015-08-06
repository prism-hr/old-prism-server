package com.zuehlke.pgadmissions.rest.representation.resource.institution;

import com.zuehlke.pgadmissions.rest.representation.address.AddressAdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

public class InstitutionRepresentationSimple extends ResourceRepresentationSimple {

    private AddressAdvertRepresentation address;

    public AddressAdvertRepresentation getAddress() {
        return address;
    }

    public void setAddress(AddressAdvertRepresentation address) {
        this.address = address;
    }

}
