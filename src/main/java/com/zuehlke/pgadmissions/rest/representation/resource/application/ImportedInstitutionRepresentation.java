package com.zuehlke.pgadmissions.rest.representation.resource.application;

public class ImportedInstitutionRepresentation extends ImportedEntityRepresentation {

    private Integer domicile;

    public Integer getDomicile() {
        return domicile;
    }

    public void setDomicile(Integer domicile) {
        this.domicile = domicile;
    }
}
