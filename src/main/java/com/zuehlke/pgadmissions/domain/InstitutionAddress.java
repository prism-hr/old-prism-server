package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Joiner;

@Entity
@Table(name = "INSTITUTION_ADDRESS")
public class InstitutionAddress extends GeocodableLocation {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_domicile_id", nullable = false)
    private InstitutionDomicile domicile;

    @ManyToOne
    @JoinColumn(name = "institution_domicile_region_id")
    private InstitutionDomicileRegion region;

    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "address_town", nullable = false)
    private String addressTown;

    @Column(name = "address_district")
    private String addressDistrict;

    @Column(name = "address_code")
    private String addressCode;

    @Embedded
    private GeographicLocation location;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public InstitutionDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(InstitutionDomicile domicile) {
        this.domicile = domicile;
    }

    public InstitutionDomicileRegion getRegion() {
        return region;
    }

    public void setRegion(InstitutionDomicileRegion region) {
        this.region = region;
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

    public String getAddressDistrict() {
        return addressDistrict;
    }

    public void setAddressDistrict(String addressDistrict) {
        this.addressDistrict = addressDistrict;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    @Override
    public final GeographicLocation getLocation() {
        return location;
    }

    @Override
    public final void setLocation(GeographicLocation location) {
        this.location = location;
    }

    public InstitutionAddress withDomicile(InstitutionDomicile domicile) {
        this.domicile = domicile;
        return this;
    }

    public InstitutionAddress withRegion(InstitutionDomicileRegion region) {
        this.region = region;
        return this;
    }

    public InstitutionAddress withAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public InstitutionAddress withAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }

    public InstitutionAddress withAddressTown(String addressTown) {
        this.addressTown = addressTown;
        return this;
    }

    public InstitutionAddress withAddressDistrict(String addressDistrict) {
        this.addressDistrict = addressDistrict;
        return this;
    }

    public InstitutionAddress withAddressCode(String addressCode) {
        this.addressCode = addressCode;
        return this;
    }

    public InstitutionAddress withLocation(GeographicLocation location) {
        this.location = location;
        return this;
    }

    @Override
    public String getLocationString() {
        return Joiner.on(", ").join(getLocationTokens());
    }
    
    public String[] getLocationTokens() {
        return filterLocationTokens(addressLine1, addressLine2, addressTown, addressDistrict, region == null ? null : region.getName(), domicile.getName());
    }

}
