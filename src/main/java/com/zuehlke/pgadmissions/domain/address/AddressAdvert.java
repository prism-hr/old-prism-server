package com.zuehlke.pgadmissions.domain.address;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.GeocodableLocation;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;

@Entity
@Table(name = "advert_address")
public class AddressAdvert extends GeocodableLocation implements AddressDefinition<ImportedAdvertDomicile> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_domicile_id", nullable = false)
    private ImportedAdvertDomicile domicile;

    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "address_town", nullable = false)
    private String addressTown;

    @Column(name = "address_region")
    private String addressRegion;

    @Column(name = "address_code")
    private String addressCode;

    @Column(name = "google_id")
    private String googleId;

    @Embedded
    private GeographicLocation location;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public ImportedAdvertDomicile getDomicile() {
        return domicile;
    }

    @Override
    public void setDomicile(ImportedAdvertDomicile domicile) {
        this.domicile = domicile;
    }

    @Override
    public String getAddressLine1() {
        return addressLine1;
    }

    @Override
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    @Override
    public String getAddressLine2() {
        return addressLine2;
    }

    @Override
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @Override
    public String getAddressTown() {
        return addressTown;
    }

    @Override
    public void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }

    @Override
    public String getAddressRegion() {
        return addressRegion;
    }

    @Override
    public void setAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
    }

    @Override
    public String getAddressCode() {
        return addressCode;
    }

    @Override
    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    @Override
    public GeographicLocation getLocation() {
        return location;
    }

    @Override
    public void setLocation(GeographicLocation location) {
        this.location = location;
    }

    public AddressAdvert withDomicile(ImportedAdvertDomicile domicile) {
        this.domicile = domicile;
        return this;
    }

    public AddressAdvert withGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

    public AddressAdvert withLocation(GeographicLocation location) {
        this.location = location;
        return this;
    }

    @Override
    public String getLocationString() {
        return getLocationString() + ", " + domicile.getName();
    }

}
