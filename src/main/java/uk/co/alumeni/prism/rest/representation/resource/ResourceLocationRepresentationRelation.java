package uk.co.alumeni.prism.rest.representation.resource;

import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;

public class ResourceLocationRepresentationRelation extends ResourceRepresentationRelation {

    private AddressRepresentation address;

    public AddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(AddressRepresentation address) {
        this.address = address;
    }
    
}
