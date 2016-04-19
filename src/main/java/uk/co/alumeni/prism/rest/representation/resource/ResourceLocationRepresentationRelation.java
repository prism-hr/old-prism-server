package uk.co.alumeni.prism.rest.representation.resource;

import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;

public class ResourceLocationRepresentationRelation extends ResourceRepresentationRelation {

    private AddressRepresentation address;

    private Boolean selected;

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public AddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(AddressRepresentation address) {
        this.address = address;
    }

}
