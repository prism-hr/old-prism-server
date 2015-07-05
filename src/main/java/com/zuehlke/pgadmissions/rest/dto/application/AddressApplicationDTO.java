package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.address.Address;

public class AddressApplicationDTO extends Address {

    @NotNull
    private Integer domicile;

    public Integer getDomicile() {
        return domicile;
    }

    public void setDomicile(Integer domicile) {
        this.domicile = domicile;
    }

}
