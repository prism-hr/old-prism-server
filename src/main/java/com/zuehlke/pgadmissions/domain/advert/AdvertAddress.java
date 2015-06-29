package com.zuehlke.pgadmissions.domain.advert;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.domain.location.GeocodableLocation;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;

@Entity
@Table(name = "advert_address")
public class AdvertAddress extends GeocodableLocation {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_domicile_id", nullable = false)
    private AdvertDomicile domicile;

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

    public AdvertDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(AdvertDomicile domicile) {
        this.domicile = domicile;
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

    public void setAddressRegion(String addressDistrict) {
        this.addressRegion = addressDistrict;
    }

    public String getAddressCode() {
        return addressCode;
    }

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

    public AdvertAddress withDomicile(AdvertDomicile domicile) {
        this.domicile = domicile;
        return this;
    }

    public AdvertAddress withAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public AdvertAddress withAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }

    public AdvertAddress withAddressCode(String addressCode) {
        this.addressCode = addressCode;
        return this;
    }

    public AdvertAddress withAddressTown(String addressTown) {
        this.addressTown = addressTown;
        return this;
    }

    public AdvertAddress withAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
        return this;
    }

    public AdvertAddress withGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

    public AdvertAddress withLocation(GeographicLocation location) {
        this.location = location;
        return this;
    }

    @Override
    public String getLocationString() {
        return Joiner.on(", ").join(getAddressTokens()) + ", " + domicile.getName();
    }

    public List<String> getAddressTokens() {
        return filterLocationTokens(addressLine1, addressLine2, addressTown, addressRegion, addressCode);
    }

}
