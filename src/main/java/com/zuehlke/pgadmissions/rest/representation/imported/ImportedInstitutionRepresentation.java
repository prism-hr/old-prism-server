package com.zuehlke.pgadmissions.rest.representation.imported;

public class ImportedInstitutionRepresentation extends ImportedEntitySimpleRepresentation {

    private ImportedEntitySimpleRepresentation domicile;

    private String ucasId;

    private String facebookId;

    public ImportedEntitySimpleRepresentation getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedEntitySimpleRepresentation domicile) {
        this.domicile = domicile;
    }

    public String getUcasId() {
        return ucasId;
    }

    public void setUcasId(String ucasId) {
        this.ucasId = ucasId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

}
