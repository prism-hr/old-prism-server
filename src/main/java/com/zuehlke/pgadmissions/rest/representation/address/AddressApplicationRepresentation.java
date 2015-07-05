package com.zuehlke.pgadmissions.rest.representation.address;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

import com.zuehlke.pgadmissions.domain.address.Address;

public class AddressApplicationRepresentation extends Address {

    private ImportedEntityResponse domicile;

    public ImportedEntityResponse getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedEntityResponse domicile) {
        this.domicile = domicile;
    }

    public AddressApplicationRepresentation withDomicile(ImportedEntityResponse domicile) {
        this.domicile = domicile;
        return this;
    }

}
