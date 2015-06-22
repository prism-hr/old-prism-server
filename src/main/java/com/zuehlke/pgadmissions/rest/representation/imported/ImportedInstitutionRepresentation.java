package com.zuehlke.pgadmissions.rest.representation.imported;

public class ImportedInstitutionRepresentation extends ImportedEntitySimpleRepresentation {

    private Integer domicile;

    public Integer getDomicile() {
        return domicile;
    }

    public void setDomicile(Integer domicile) {
        this.domicile = domicile;
    }

}
