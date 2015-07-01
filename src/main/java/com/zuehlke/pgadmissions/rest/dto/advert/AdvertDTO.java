package com.zuehlke.pgadmissions.rest.dto.advert;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;
import com.zuehlke.pgadmissions.rest.validation.annotation.PhoneNumber;

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

    @NotEmpty
    @PhoneNumber
    private String telephone;

    @Valid
    private AddressAdvertDTO address;

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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public AddressAdvertDTO getAddress() {
        return address;
    }

    public void setAddress(AddressAdvertDTO address) {
        this.address = address;
    }

}
