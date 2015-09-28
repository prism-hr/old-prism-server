package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.address.AddressDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismDomicile;

public class AddressDTO extends AddressDefinition<PrismDomicile> {

    private Integer id;

    private String addressLine1;

    private String addressLine2;

    private String addressTown;

    private String addressRegion;

    private String addressCode;

    @NotNull
    private PrismDomicile domicile;

    private String googleId;

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

    public PrismDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(PrismDomicile domicile) {
        this.domicile = domicile;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

}
