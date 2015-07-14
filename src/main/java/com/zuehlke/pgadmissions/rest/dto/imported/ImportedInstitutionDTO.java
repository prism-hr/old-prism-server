package com.zuehlke.pgadmissions.rest.dto.imported;

import javax.validation.constraints.NotNull;

public class ImportedInstitutionDTO extends ImportedEntityDTO {

    @NotNull
    private ImportedEntityDTO domicile;

    public ImportedEntityDTO getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedEntityDTO domicile) {
        this.domicile = domicile;
    }

}
