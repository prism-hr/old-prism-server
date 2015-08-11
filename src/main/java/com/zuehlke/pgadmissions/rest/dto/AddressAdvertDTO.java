package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedAdvertDomicileDTO;

public class AddressAdvertDTO extends Address implements AddressDefinition<ImportedAdvertDomicileDTO> {

    @NotNull
    @Valid
    private ImportedAdvertDomicileDTO domicile;

    private String googleId;

    @Override
    public ImportedAdvertDomicileDTO getDomicile() {
        return domicile;
    }

    @Override
    public void setDomicile(ImportedAdvertDomicileDTO domicile) {
        this.domicile = domicile;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

}
