package com.zuehlke.pgadmissions.rest.representation.address;

import com.zuehlke.pgadmissions.domain.address.Address;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;
import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

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
    
    public AddressApplicationRepresentation withAddressLine1(String addressLine1) {
        setAddressLine1(addressLine1);
        return this;
    }

    public AddressApplicationRepresentation withAddressTown(String addressTown) {
        setAddressTown(addressTown);
        return this;
    }
    
    public AddressApplicationRepresentation withAddressCode(String addressCode) {
        setAddressCode(addressCode);
        return this;
    }
    
}
