package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.utils.validation.DatePast;
import uk.co.alumeni.prism.utils.validation.PhoneNumber;

public class ApplicationPersonalDetailDTO {

    @NotNull
    @Valid
    private ApplicationPersonalDetailUserDTO user;

    @Size(min = 6, max = 32)
    private String skype;

    @NotEmpty
    @PhoneNumber
    private String phone;

    private Boolean firstLanguageLocale;

    @Valid
    private ApplicationLanguageQualificationDTO languageQualification;

    private Boolean visaRequired;

    @Valid
    private ApplicationPassportDTO passport;

    @NotNull
    private Integer firstNationality;

    private Integer secondNationality;

    private Integer title;

    @NotNull
    private Integer gender;

    @NotNull
    @DatePast
    private LocalDate dateOfBirth;

    @NotNull
    private Integer country;

    private Integer ethnicity;

    private Integer disability;

    private Integer domicile;

    @Size(min = 1)
    private String studyLocation;

    @Size(min = 1)
    private String studyDivision;

    @Size(min = 1)
    private String studyArea;

    public ApplicationPersonalDetailUserDTO getUser() {
        return user;
    }

    public void setUser(ApplicationPersonalDetailUserDTO user) {
        this.user = user;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getFirstLanguageLocale() {
        return firstLanguageLocale;
    }

    public void setFirstLanguageLocale(Boolean firstLanguageLocale) {
        this.firstLanguageLocale = firstLanguageLocale;
    }

    public ApplicationLanguageQualificationDTO getLanguageQualification() {
        return languageQualification;
    }

    public void setLanguageQualification(ApplicationLanguageQualificationDTO languageQualification) {
        this.languageQualification = languageQualification;
    }

    public Boolean getVisaRequired() {
        return visaRequired;
    }

    public void setVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
    }

    public ApplicationPassportDTO getPassport() {
        return passport;
    }

    public void setPassport(ApplicationPassportDTO passport) {
        this.passport = passport;
    }

    public Integer getFirstNationality() {
        return firstNationality;
    }

    public void setFirstNationality(Integer firstNationality) {
        this.firstNationality = firstNationality;
    }

    public Integer getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(Integer secondNationality) {
        this.secondNationality = secondNationality;
    }

    public Integer getTitle() {
        return title;
    }

    public void setTitle(Integer title) {
        this.title = title;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public Integer getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(Integer ethnicity) {
        this.ethnicity = ethnicity;
    }

    public Integer getDisability() {
        return disability;
    }

    public void setDisability(Integer disability) {
        this.disability = disability;
    }

    public Integer getDomicile() {
        return domicile;
    }

    public void setDomicile(Integer domicile) {
        this.domicile = domicile;
    }

    public String getStudyLocation() {
        return studyLocation;
    }

    public void setStudyLocation(String studyLocation) {
        this.studyLocation = studyLocation;
    }

    public String getStudyDivision() {
        return studyDivision;
    }

    public void setStudyDivision(String studyDivision) {
        this.studyDivision = studyDivision;
    }

    public String getStudyArea() {
        return studyArea;
    }

    public void setStudyArea(String studyArea) {
        this.studyArea = studyArea;
    }
}
