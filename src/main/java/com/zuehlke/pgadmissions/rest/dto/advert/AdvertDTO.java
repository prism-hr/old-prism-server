package com.zuehlke.pgadmissions.rest.dto.advert;

import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import uk.co.alumeni.prism.utils.validation.PhoneNumber;

import javax.validation.Valid;
import javax.validation.constraints.Size;

public class AdvertDTO {

    @NotEmpty
    @Size(max = 1000)
    private String summary;

    @URL
    @Size(max = 2048)
    private String homepage;

    @URL
    @Size(max = 2048)
    private String applyHomepage;

    @NotEmpty
    @PhoneNumber
    private String telephone;

    @Valid
    private AddressAdvertDTO address;

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

    public AddressAdvertDTO getAddress() {
        return address;
    }

    public void setAddress(AddressAdvertDTO address) {
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
