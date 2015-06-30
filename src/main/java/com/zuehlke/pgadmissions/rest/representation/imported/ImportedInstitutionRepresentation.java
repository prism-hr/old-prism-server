package com.zuehlke.pgadmissions.rest.representation.imported;

public class ImportedInstitutionRepresentation extends ImportedEntitySimpleRepresentation {

    private Integer domicile;
    
    private String ucasId;
    
    private String facebookId;

    public Integer getDomicile() {
        return domicile;
    }

    public void setDomicile(Integer domicile) {
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
