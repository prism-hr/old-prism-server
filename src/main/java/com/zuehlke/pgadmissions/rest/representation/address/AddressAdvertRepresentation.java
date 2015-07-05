package com.zuehlke.pgadmissions.rest.representation.address;

import java.math.BigDecimal;

import uk.co.alumeni.prism.api.model.imported.response.ImportedAdvertDomicileResponse;

import com.zuehlke.pgadmissions.domain.address.Address;

public class AddressAdvertRepresentation extends Address {

    private ImportedAdvertDomicileResponse domicile;

    private String googleId;

    private BigDecimal locationX;

    private BigDecimal locationY;

    private String locationString;

    public ImportedAdvertDomicileResponse getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedAdvertDomicileResponse domicile) {
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
