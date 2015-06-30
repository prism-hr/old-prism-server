package com.zuehlke.pgadmissions.rest.dto.advert;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import com.zuehlke.pgadmissions.rest.dto.AdvertAddressDTO;

public class AdvertDetailsDTO {

    @Size(max = 20000)
    private String description;

    @URL
    @Size(max = 2048)
    private String homepage;

    private AdvertAddressDTO address;

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

    public AdvertAddressDTO getAddress() {
        return address;
    }

    public void setAddress(AdvertAddressDTO address) {
        this.address = address;
    }

}
