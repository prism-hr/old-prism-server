package com.zuehlke.pgadmissions.rest.representation.address;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;
import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import com.zuehlke.pgadmissions.domain.address.Address;

public class AddressApplicationRepresentation extends Address implements AddressDefinition<ImportedEntityResponse> {

    private ImportedEntityResponse domicile;

    @Override
    public ImportedEntityResponse getDomicile() {
        return domicile;
    }

    @Override
    public void setDomicile(ImportedEntityResponse domicile) {
        this.domicile = domicile;
    }

    public AddressApplicationRepresentation withDomicile(ImportedEntityResponse domicile) {
        this.domicile = domicile;
        return this;
    }

}
