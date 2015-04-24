package com.zuehlke.pgadmissions.domain.institution;

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
@Table(name = "INSTITUTION_ADDRESS")
public class InstitutionAddress extends GeocodableLocation {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_domicile_id", nullable = false)
    private InstitutionDomicile domicile;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

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

    public final Institution getInstitution() {
        return institution;
    }

    public final void setInstitution(Institution institution) {
        this.institution = institution;
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

    @Override
    public final GeographicLocation getLocation() {
        return location;
    }

    @Override
    public final void setLocation(GeographicLocation location) {
        this.location = location;
    }


    public InstitutionAddress withInstitution(final Institution institution) {
        this.institution = institution;
        return this;
    }

    public InstitutionAddress withDomicile(final InstitutionDomicile domicile) {
        this.domicile = domicile;
        return this;
    }
    
    public InstitutionAddress withAddressLine1(final String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public InstitutionAddress withAddressLine2(final String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }
    
    public InstitutionAddress withAddressCode(final String addressCode) {
        this.addressCode = addressCode;
        return this;
    }

    public InstitutionAddress withAddressTown(final String addressTown) {
        this.addressTown = addressTown;
        return this;
    }
    
    public InstitutionAddress withAddressRegion(final String addressRegion) {
        this.addressRegion = addressRegion;
        return this;
    }
    
    public InstitutionAddress withLocation(final GeographicLocation location) {
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
