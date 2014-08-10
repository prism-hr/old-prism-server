package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class InstitutionDTO {

    @NotEmpty
    private String domicile;

    @NotEmpty
    private String name;

    @NotEmpty
    private String homepage;
    
    @NotEmpty
    private Integer logoDocumentId;

    @NotNull
    private InstitutionAddressDTO address;
    
    @NotNull
    private Boolean uclInstitution = false;

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public final Integer getLogoDocumentId() {
        return logoDocumentId;
    }

    public final void setLogoDocumentId(Integer logoDocumentId) {
        this.logoDocumentId = logoDocumentId;
    }

    public InstitutionAddressDTO getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressDTO address) {
        this.address = address;
    }

    public final Boolean getUclInstitution() {
        return uclInstitution;
    }

    public final void setUclInstitution(Boolean uclInstitution) {
        this.uclInstitution = uclInstitution;
    }
    
}
