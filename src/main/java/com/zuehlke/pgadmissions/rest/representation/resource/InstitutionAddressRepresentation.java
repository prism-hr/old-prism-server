package com.zuehlke.pgadmissions.rest.representation.resource;

import java.math.BigDecimal;

import com.zuehlke.pgadmissions.rest.representation.AddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.InstitutionDomicileRepresentation;

public class InstitutionAddressRepresentation extends AddressRepresentation {

    private InstitutionDomicileRepresentation domicile;

    private String googleId;

    private BigDecimal locationX;

    private BigDecimal locationY;

    private String locationString;

    public InstitutionDomicileRepresentation getDomicile() {
        return domicile;
    }

    public void setDomicile(InstitutionDomicileRepresentation domicile) {
        this.domicile = domicile;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public BigDecimal getLocationX() {
        return locationX;
    }

    public void setLocationX(BigDecimal locationX) {
        this.locationX = locationX;
    }

    public BigDecimal getLocationY() {
        return locationY;
    }

    public void setLocationY(BigDecimal locationY) {
        this.locationY = locationY;
    }

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

    public InstitutionAddressRepresentation withDomicile(InstitutionDomicileRepresentation domicile) {
        this.domicile = domicile;
        return this;
    }

    public InstitutionAddressRepresentation withAddressLine1(String addressLine1) {
        setAddressLine1(addressLine1);
        return this;
    }

    public InstitutionAddressRepresentation withAddressLine2(String addressLine2) {
        setAddressLine2(addressLine2);
        return this;
    }

    public InstitutionAddressRepresentation withAddressTown(String addressTown) {
        setAddressTown(addressTown);
        return this;
    }

    public InstitutionAddressRepresentation withAddressRegion(String addressRegion) {
        setAddressRegion(addressRegion);
        return this;
    }

    public InstitutionAddressRepresentation withAddressCode(String addressCode) {
        setAddressCode(addressCode);
        return this;
    }

    public InstitutionAddressRepresentation withGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

}
