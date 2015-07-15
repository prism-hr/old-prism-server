package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;
import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import javax.validation.constraints.NotNull;

public class AddressApplicationDTO extends Address implements AddressDefinition<ImportedEntityDTO> {

    @NotNull
    private ImportedEntityDTO domicile;

    @Override
    public ImportedEntityDTO getDomicile() {
        return domicile;
    }

    @Override
    public void setDomicile(ImportedEntityDTO domicile) {
        this.domicile = domicile;
    }

}
