package com.zuehlke.pgadmissions.rest.dto.advert;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import com.zuehlke.pgadmissions.rest.dto.AddressDTO;

import uk.co.alumeni.prism.utils.validation.PhoneNumber;

public class AdvertDTO {

    @Size(max = 1000)
    private String summary;

    @URL
    @Size(max = 2048)
    private String homepage;

    @URL
    @Size(max = 2048)
    private String applyHomepage;

    @PhoneNumber
    private String telephone;

    @Valid
    private AddressDTO address;

    @Valid
    private AdvertCategoriesDTO categories;

    @Valid
    private AdvertTargetsDTO targets;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
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

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public AdvertCategoriesDTO getCategories() {
        return categories;
    }

    public void setCategories(AdvertCategoriesDTO categories) {
        this.categories = categories;
    }

    public AdvertTargetsDTO getTargets() {
        return targets;
    }

    public void setTargets(AdvertTargetsDTO targets) {
        this.targets = targets;
    }

}
