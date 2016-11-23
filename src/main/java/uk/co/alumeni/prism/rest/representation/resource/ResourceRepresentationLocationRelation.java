package uk.co.alumeni.prism.rest.representation.resource;

import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;

import static org.apache.commons.lang3.ObjectUtils.compare;

public class ResourceRepresentationLocationRelation extends ResourceRepresentationRelation {

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

    public ResourceRepresentationLocationRelation withAddress(AddressRepresentation address) {
        this.address = address;
        return this;
    }

    public ResourceRepresentationLocationRelation withSelected(Boolean selected) {
        this.selected = selected;
        return this;
    }

    @Override
    public int compareTo(ResourceRepresentationIdentity other) {
        int compare = super.compareTo(other);
        if (compare == 0 && ResourceRepresentationLocationRelation.class.isAssignableFrom(other.getClass())) {
            return compare(address, ((ResourceRepresentationLocationRelation) other).getAddress());
        }
        return compare;
    }

}
