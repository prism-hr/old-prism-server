package com.zuehlke.pgadmissions.rest.domain.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.Gender;

public class PersonalDetailsRepresentation {

    private String messenger;

    private String phoneNumber;

    private Boolean firstLanguageEnglish;

    private LanguageQualificationRepresentation languageQualification;

    private Boolean visaRequired;

    private PassportRepresentation passport;

    private String firstNationality;

    private String secondNationality;

    private String title;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String country;

    private String ethnicity;

    private String disability;

    private String residenceCountry;

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

    public String getFirstNationality() {
        return firstNationality;
    }

    public void setFirstNationality(String firstNationality) {
        this.firstNationality = firstNationality;
    }

    public String getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(String secondNationality) {
        this.secondNationality = secondNationality;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getDisability() {
        return disability;
    }

    public void setDisability(String disability) {
        this.disability = disability;
    }

    public String getResidenceCountry() {
        return residenceCountry;
    }

    public void setResidenceCountry(String residenceCountry) {
        this.residenceCountry = residenceCountry;
    }
}
