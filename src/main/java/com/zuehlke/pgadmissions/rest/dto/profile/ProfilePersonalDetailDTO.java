package com.zuehlke.pgadmissions.rest.dto.profile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismDomicile;
import com.zuehlke.pgadmissions.domain.definitions.PrismGender;

import uk.co.alumeni.prism.utils.validation.DatePast;
import uk.co.alumeni.prism.utils.validation.PhoneNumber;

public class ProfilePersonalDetailDTO {

    @NotNull
    private PrismGender gender;

    @NotNull
    @DatePast
    private LocalDate dateOfBirth;

    @NotNull
    private PrismDomicile domicile;

    @NotNull
    private PrismDomicile nationality;

    @NotNull
    private Boolean visaRequired;

    @NotEmpty
    @PhoneNumber
    private String phone;

    @Size(min = 6, max = 32)
    private String skype;

    public PrismGender getGender() {
        return gender;
    }

    public void setGender(PrismGender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public PrismDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(PrismDomicile domicile) {
        this.domicile = domicile;
    }

    public PrismDomicile getNationality() {
        return nationality;
    }

    public void setNationality(PrismDomicile nationality) {
        this.nationality = nationality;
    }

    public Boolean getVisaRequired() {
        return visaRequired;
    }

    public void setVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

}
