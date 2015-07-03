package com.zuehlke.pgadmissions.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.address.Address;

public class AddressAdvertDTO extends Address {

    @NotEmpty
    private String domicile;

    private String googleId;

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

}
