package uk.co.alumeni.prism.domain.address;

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
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import uk.co.alumeni.prism.domain.Domicile;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
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

    public Set<AddressLocation> getAddressLocations() {
        return locations;
    }

    public void addAddressLocationPart(AddressLocation location) {
        locations.add(location);
    }

    public String getLocationString() {
        return Joiner.on(", ").skipNulls().join(addressLine1, addressLine2, addressTown, addressRegion, addressCode, domicile.getId().name());
    }

    public List<String> getLocationTokens() {
        List<String> tokens = Lists.newLinkedList();

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

}
