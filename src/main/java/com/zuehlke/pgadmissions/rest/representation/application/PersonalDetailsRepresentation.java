package com.zuehlke.pgadmissions.rest.representation.application;

import org.joda.time.LocalDate;

public class PersonalDetailsRepresentation {

    private String messenger;

    private String phoneNumber;

    private Boolean firstLanguageEnglish;

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

    private Integer residenceCountry;

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getFirstLanguageEnglish() {
        return firstLanguageEnglish;
    }

    public void setFirstLanguageEnglish(Boolean firstLanguageEnglish) {
        this.firstLanguageEnglish = firstLanguageEnglish;
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

    public Integer getResidenceCountry() {
        return residenceCountry;
    }

    public void setResidenceCountry(Integer residenceCountry) {
        this.residenceCountry = residenceCountry;
    }
}
