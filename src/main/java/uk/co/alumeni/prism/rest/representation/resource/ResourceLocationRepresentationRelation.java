package uk.co.alumeni.prism.rest.representation.resource;

import static org.apache.commons.lang3.ObjectUtils.compare;
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

    @Override
    public int compareTo(ResourceRepresentationIdentity other) {
        int compare = super.compareTo(other);
        if (compare == 0 && ResourceLocationRepresentationRelation.class.isAssignableFrom(other.getClass())) {
            return compare(address, ((ResourceLocationRepresentationRelation) other).getAddress());
        }
        return compare;
    }

}
