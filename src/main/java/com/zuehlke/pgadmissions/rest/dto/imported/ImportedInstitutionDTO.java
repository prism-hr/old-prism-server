package com.zuehlke.pgadmissions.rest.dto.imported;

import javax.validation.constraints.NotNull;

public class ImportedInstitutionDTO extends ImportedEntitySimpleDTO {

    @NotNull
    private Integer domicile;

    public Integer getDomicile() {
        return domicile;
    }

    public void setDomicile(Integer domicile) {
        this.domicile = domicile;
    }

}
