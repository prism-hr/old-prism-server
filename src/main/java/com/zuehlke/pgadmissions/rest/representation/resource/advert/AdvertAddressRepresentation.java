package com.zuehlke.pgadmissions.rest.representation.resource.advert;

import java.math.BigDecimal;

import com.zuehlke.pgadmissions.rest.representation.AddressRepresentation;

public class AdvertAddressRepresentation extends AddressRepresentation {

    private AdvertDomicileRepresentation domicile;

    private String googleId;

    private BigDecimal locationX;

    private BigDecimal locationY;

    private String locationString;

    public AdvertDomicileRepresentation getDomicile() {
        return domicile;
    }

    public void setDomicile(AdvertDomicileRepresentation domicile) {
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

    public AdvertAddressRepresentation withDomicile(AdvertDomicileRepresentation domicile) {
        this.domicile = domicile;
        return this;
    }

    public AdvertAddressRepresentation withAddressLine1(String addressLine1) {
        setAddressLine1(addressLine1);
        return this;
    }

    public AdvertAddressRepresentation withAddressLine2(String addressLine2) {
        setAddressLine2(addressLine2);
        return this;
    }

    public AdvertAddressRepresentation withAddressTown(String addressTown) {
        setAddressTown(addressTown);
        return this;
    }

    public AdvertAddressRepresentation withAddressRegion(String addressRegion) {
        setAddressRegion(addressRegion);
        return this;
    }

    public AdvertAddressRepresentation withAddressCode(String addressCode) {
        setAddressCode(addressCode);
        return this;
    }

    public AdvertAddressRepresentation withGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

}
