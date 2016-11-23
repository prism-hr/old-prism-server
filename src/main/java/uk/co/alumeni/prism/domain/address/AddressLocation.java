package uk.co.alumeni.prism.domain.address;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.UniqueEntity;

import javax.persistence.*;

import static com.google.common.base.Objects.equal;

@Entity
@Table(name = "address_location", uniqueConstraints = {@UniqueConstraint(columnNames = {"address_id", "address_location_part_id"})})
public class AddressLocation implements UniqueEntity {

    @Id
    @GeneratedValue
    Integer id;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ManyToOne
    @JoinColumn(name = "address_location_part_id", nullable = false)
    private AddressLocationPart locationPart;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public AddressLocationPart getLocationPart() {
        return locationPart;
    }

    public void setLocationPart(AddressLocationPart locationPart) {
        this.locationPart = locationPart;
    }

    public AddressLocation withAddress(Address address) {
        this.address = address;
        return this;
    }

    public AddressLocation withLocationPart(AddressLocationPart locationPart) {
        this.locationPart = locationPart;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(address, locationPart);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        AddressLocation other = (AddressLocation) object;
        return equal(address, other.getAddress()) && Objects.equal(locationPart, other.getLocationPart());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("address", address).addProperty("locationPart", locationPart);
    }

}
