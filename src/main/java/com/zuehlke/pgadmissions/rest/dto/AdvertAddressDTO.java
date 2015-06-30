package com.zuehlke.pgadmissions.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;

public class AdvertAddressDTO {

    @NotEmpty
    private String domicile;

    @NotEmpty
    private String addressLine1;

    private String addressLine2;

    @NotEmpty
    private String addressTown;

    private String addressRegion;

    private String addressCode;

    private String googleId;

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
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

    public void setAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
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
    
    public AdvertAddressDTO withDomicile(String domicile) {
        this.domicile = domicile;
        return this;
    }

    public AdvertAddressDTO withAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public AdvertAddressDTO withAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }
    
    public AdvertAddressDTO withAddressTown(String addressTown) {
        this.addressTown = addressTown;
        return this;
    }

    public AdvertAddressDTO withAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
        return this;
    }
    
    public AdvertAddressDTO withAddressCode(String addressCode) {
        this.addressCode = addressCode;
        return this;
    }
    
    public AdvertAddressDTO withGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }
    
}

