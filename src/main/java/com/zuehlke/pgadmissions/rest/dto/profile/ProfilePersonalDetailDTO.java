package com.zuehlke.pgadmissions.rest.dto.profile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;

import uk.co.alumeni.prism.utils.validation.DatePast;
import uk.co.alumeni.prism.utils.validation.PhoneNumber;

public class ProfilePersonalDetailDTO {

    private ImportedEntityDTO title;

    @NotNull
    private ImportedEntityDTO gender;

    @NotNull
    @DatePast
    private LocalDate dateOfBirth;

    private ImportedEntityDTO domicile;

    @NotNull
    private ImportedEntityDTO nationality;

    private Boolean visaRequired;

    @NotEmpty
    @PhoneNumber
    private String phone;

    @Size(min = 6, max = 32)
    private String skype;

    private ImportedEntityDTO ethnicity;

    private ImportedEntityDTO disability;

    public ImportedEntityDTO getTitle() {
        return title;
    }

    public void setTitle(ImportedEntityDTO title) {
        this.title = title;
    }

    public ImportedEntityDTO getGender() {
        return gender;
    }

    public void setGender(ImportedEntityDTO gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public ImportedEntityDTO getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedEntityDTO domicile) {
        this.domicile = domicile;
    }

    public ImportedEntityDTO getNationality() {
        return nationality;
    }

    public void setNationality(ImportedEntityDTO nationality) {
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

    public ImportedEntityDTO getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(ImportedEntityDTO ethnicity) {
        this.ethnicity = ethnicity;
    }

    public ImportedEntityDTO getDisability() {
        return disability;
    }

    public void setDisability(ImportedEntityDTO disability) {
        this.disability = disability;
    }

}
