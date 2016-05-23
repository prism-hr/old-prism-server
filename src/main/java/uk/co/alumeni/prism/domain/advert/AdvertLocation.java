package uk.co.alumeni.prism.domain.advert;

import static com.google.common.base.Objects.equal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import uk.co.alumeni.prism.domain.address.Address;

import com.google.common.base.Objects;

@Entity
@Table(name = "advert_location", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "location_advert_id", "address_id" }) })
public class AdvertLocation extends AdvertAttribute {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "location_advert_id", nullable = false)
    private Advert locationAdvert;

    @OneToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Advert getLocationAdvert() {
        return locationAdvert;
    }

    public void setLocationAdvert(Advert locationAdvert) {
        this.locationAdvert = locationAdvert;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public AdvertLocation withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertLocation withLocationAdvert(Advert locationAdvert) {
        this.locationAdvert = locationAdvert;
        return this;
    }

    public AdvertLocation withAddress(Address address) {
        this.address = address;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(advert, locationAdvert, address);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        AdvertLocation other = (AdvertLocation) object;
        return equal(advert, other.getAdvert()) && equal(locationAdvert, other.getLocationAdvert()) && equal(address, other.getAddress());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("locationAdvert", locationAdvert).addProperty("address", address);
    }

}
