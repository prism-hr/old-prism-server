package uk.co.alumeni.prism.domain.address;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import uk.co.alumeni.prism.domain.UniqueEntity;

import com.google.common.base.Objects;

@Entity
@Table(name = "address_location", uniqueConstraints = { @UniqueConstraint(columnNames = { "address_id", "address_location_part_id" }) })
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
        return Objects.equal(address.getId(), other.getAddress().getId()) && Objects.equal(locationPart.getId(), other.getLocationPart().getId());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("address", address).addProperty("locationPart", locationPart);
    }

}
