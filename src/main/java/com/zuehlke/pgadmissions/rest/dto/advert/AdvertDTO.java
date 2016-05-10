package com.zuehlke.pgadmissions.rest.dto.advert;

import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.validation.annotation.PhoneNumber;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class AdvertDTO {

    @NotEmpty
    @Size(max = 255)
    private String title;

    @NotEmpty
    @Size(max = 1000)
    private String summary;

    @Pattern(regexp = "^(mailto:|https?://).+$")
    @Size(max = 2048)
    private String applyHomepage;

    @NotEmpty
    @PhoneNumber
    private String telephone;

    @Valid
    private InstitutionAddressDTO address;

    private BigDecimal sponsorshipRequired;

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

    public InstitutionAddressDTO getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressDTO address) {
        this.address = address;
    }

    public BigDecimal getSponsorshipRequired() {
        return sponsorshipRequired;
    }

    public void setSponsorshipRequired(BigDecimal sponsorshipRequired) {
        this.sponsorshipRequired = sponsorshipRequired;
    }

}
