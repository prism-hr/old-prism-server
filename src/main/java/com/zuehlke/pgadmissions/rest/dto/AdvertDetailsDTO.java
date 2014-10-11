package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

public class AdvertDetailsDTO {

    @Size(max = 20000)
    private String description;

    @URL
    @Size(max = 2048)
    private String applyLink;

    private InstitutionAddressDTO address;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplyLink() {
        return applyLink;
    }

    public void setApplyLink(String applyLink) {
        this.applyLink = applyLink;
    }

    public InstitutionAddressDTO getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressDTO address) {
        this.address = address;
    }

}
