package uk.co.alumeni.prism.domain.address;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newLinkedList;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.advert.Advert;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;

@Entity
@Table(name = "address")
public class Address extends AddressDefinition<Domicile> {

    @Id
    @GeneratedValue
    private Integer id;

    @NotEmpty
    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @NotEmpty
    @Column(name = "address_town")
    private String addressTown;

    @Column(name = "address_region")
    private String addressRegion;

    @Column(name = "address_code")
    private String addressCode;

    @ManyToOne
    @JoinColumn(name = "domicile_id")
    private Domicile domicile;

    @Column(name = "google_id")
    private String googleId;

    @Embedded
    private AddressCoordinates addressCoordinates;

    @OneToOne(mappedBy = "address")
    private Advert advert;

    @OneToMany(mappedBy = "address")
    private Set<AddressLocation> locations = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressTown() {
        return addressTown;
    }

    public void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }

    public String getAddressRegion() {
        return addressRegion;
    }

    public void setAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public Domicile getDomicile() {
        return domicile;
    }

    public void setDomicile(Domicile domicile) {
        this.domicile = domicile;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public AddressCoordinates getAddressCoordinates() {
        return addressCoordinates;
    }

    public void setAddressCoordinates(AddressCoordinates addressCoordinates) {
        this.addressCoordinates = addressCoordinates;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Set<AddressLocation> getLocations() {
        return locations;
    }

    public void setLocations(Set<AddressLocation> locations) {
        this.locations = locations;
    }

    public String getEstablishmentName() {
        return advert == null ? null : advert.getName();
    }

    public List<String> getAddressTokens() {
        List<String> tokens = newLinkedList();

        tokens.add(getAddressLine1());

        String addressLine2 = getAddressLine2();
        if (addressLine2 != null) {
            tokens.add(addressLine2);
        }

        tokens.add(getAddressTown());

        String addressRegion = getAddressRegion();
        if (addressRegion != null) {
            tokens.add(addressRegion);
        }

        String addressCode = getAddressCode();
        if (addressCode != null) {
            tokens.add(addressCode);
        }

        return tokens;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        Address other = (Address) object;
        return equal(id, other.getId());
    }

    @Override
    public String toString() {
        return Joiner.on(", ").skipNulls().join(addressLine1, addressLine2, addressTown, addressRegion, addressCode, domicile.getId().name());
    }

}
