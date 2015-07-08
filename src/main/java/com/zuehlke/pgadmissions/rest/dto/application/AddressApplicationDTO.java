package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;

import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import com.zuehlke.pgadmissions.domain.address.Address;

public class AddressApplicationDTO extends Address implements AddressDefinition<Integer> {

    @NotNull
    private Integer domicile;

    @Override
    public Integer getDomicile() {
        return domicile;
    }

    @Override
    public void setDomicile(Integer domicile) {
        this.domicile = domicile;
    }

}
