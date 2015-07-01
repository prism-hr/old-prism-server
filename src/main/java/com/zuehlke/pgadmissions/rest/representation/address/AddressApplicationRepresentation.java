package com.zuehlke.pgadmissions.rest.representation.address;

import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;

public class AddressApplicationRepresentation extends Address {

    private ImportedEntitySimpleRepresentation domicile;

    public ImportedEntitySimpleRepresentation getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedEntitySimpleRepresentation domicile) {
        this.domicile = domicile;
    }

    public AddressApplicationRepresentation withDomicile(ImportedEntitySimpleRepresentation domicile) {
        this.domicile = domicile;
        return this;
    }

}
