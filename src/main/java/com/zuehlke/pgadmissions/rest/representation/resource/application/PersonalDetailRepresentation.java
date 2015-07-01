package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

public class PersonalDetailRepresentation extends ApplicationSectionRepresentation {

    private String skype;

    private String phone;

    private Boolean firstLanguageLocale;

    private LanguageQualificationRepresentation languageQualification;

    private Boolean visaRequired;

    private PassportRepresentation passport;

    private Integer firstNationality;

    private Integer secondNationality;

    private Integer title;

    private Integer gender;

    private LocalDate dateOfBirth;

    private Integer country;

    private Integer ethnicity;

    private Integer disability;

    private Integer domicile;

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

    public LanguageQualificationRepresentation getLanguageQualification() {
        return languageQualification;
    }

    public void setLanguageQualification(LanguageQualificationRepresentation languageQualification) {
        this.languageQualification = languageQualification;
    }

    public Boolean getVisaRequired() {
        return visaRequired;
    }

    public void setVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
    }

    public PassportRepresentation getPassport() {
        return passport;
    }

    public void setPassport(PassportRepresentation passport) {
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
}
