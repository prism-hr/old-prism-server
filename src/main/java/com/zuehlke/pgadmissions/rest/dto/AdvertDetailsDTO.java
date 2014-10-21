package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

public class AdvertDetailsDTO {

    @Size(max = 20000)
    private String description;

    @URL
    @Size(max = 2048)
    private String homepage;
    
    @URL
    @Size(max = 2048)
    private String applyHomepage;

    private InstitutionAddressDTO address;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public final String getHomepage() {
        return homepage;
    }

    public final void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getApplyHomepage() {
        return applyHomepage;
    }

    public void setApplyHomepage(String applyLink) {
        this.applyHomepage = applyLink;
    }

    public InstitutionAddressDTO getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressDTO address) {
        this.address = address;
    }

}
