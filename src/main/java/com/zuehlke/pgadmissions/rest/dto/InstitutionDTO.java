package com.zuehlke.pgadmissions.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;

public class InstitutionDTO extends AdvertDTO {

    @NotEmpty
    private String domicile;

    private String googleIdentifier;

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public String getGoogleIdentifier() {
        return googleIdentifier;
    }

    public void setGoogleIdentifier(String googleIdentifier) {
        this.googleIdentifier = googleIdentifier;
    }

}
