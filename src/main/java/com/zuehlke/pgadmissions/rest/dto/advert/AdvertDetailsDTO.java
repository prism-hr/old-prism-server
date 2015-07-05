package com.zuehlke.pgadmissions.rest.dto.advert;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;

public class AdvertDetailsDTO {

    @Size(max = 20000)
    private String description;

    @URL
    @Size(max = 2048)
    private String homepage;

    private AddressAdvertDTO address;

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

    public AddressAdvertDTO getAddress() {
        return address;
    }

    public void setAddress(AddressAdvertDTO address) {
        this.address = address;
    }

}
