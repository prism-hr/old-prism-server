package com.zuehlke.pgadmissions.rest.representation.address;

import java.math.BigDecimal;

import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertDomicileRepresentation;

public class AddressAdvertRepresentation extends Address {

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

}
