package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

public class AdvertDTO {

    @NotEmpty
    @Size(max = 255)
    private String title;

    @NotEmpty
    @Size(max = 1000)
    private String summary;

    @URL
    @Size(max = 2048)
    private String applyHomepage;

    @Valid
    private InstitutionAddressDTO address;

    @Valid
    private InstitutionPartnerDTO partner;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getApplyHomepage() {
        return applyHomepage;
    }

    public void setApplyHomepage(String applyHomepage) {
        this.applyHomepage = applyHomepage;
    }

    public InstitutionAddressDTO getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressDTO address) {
        this.address = address;
    }

    public InstitutionPartnerDTO getPartner() {
        return partner;
    }

    public void setPartner(InstitutionPartnerDTO partner) {
        this.partner = partner;
    }

}
