package com.zuehlke.pgadmissions.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;

import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import com.zuehlke.pgadmissions.domain.address.Address;

public class AddressAdvertDTO extends Address implements AddressDefinition<String> {

    @NotEmpty
    private String domicile;

    private String googleId;

    @Override
    public String getDomicile() {
        return domicile;
    }

    @Override
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
