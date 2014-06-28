package com.zuehlke.pgadmissions.rest.domain.application;

import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.Gender;

import org.joda.time.LocalDate;

public class PersonalDetailsRepresentation {

    private String messenger;

    private String phoneNumber;

    private Boolean firstLanguageEnglish;

    private LanguageQualificationRepresentation languageQualification;

    private Boolean visaRequired;

    private PassportRepresentation passport;

    private ImportedEntityRepresentation firstNationality;

    private ImportedEntityRepresentation secondNationality;

    private ImportedEntityRepresentation title;

    private Gender gender;

    private LocalDate dateOfBirth;

    private ImportedEntityRepresentation country;

    private ImportedEntityRepresentation ethnicity;

    private ImportedEntityRepresentation disability;

    private ImportedEntityRepresentation residenceCountry;

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

    public ImportedEntityRepresentation getFirstNationality() {
        return firstNationality;
    }

    public void setFirstNationality(ImportedEntityRepresentation firstNationality) {
        this.firstNationality = firstNationality;
    }

    public ImportedEntityRepresentation getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(ImportedEntityRepresentation secondNationality) {
        this.secondNationality = secondNationality;
    }

    public ImportedEntityRepresentation getTitle() {
        return title;
    }

    public void setTitle(ImportedEntityRepresentation title) {
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

    public ImportedEntityRepresentation getCountry() {
        return country;
    }

    public void setCountry(ImportedEntityRepresentation country) {
        this.country = country;
    }

    public ImportedEntityRepresentation getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(ImportedEntityRepresentation ethnicity) {
        this.ethnicity = ethnicity;
    }

    public ImportedEntityRepresentation getDisability() {
        return disability;
    }

    public void setDisability(ImportedEntityRepresentation disability) {
        this.disability = disability;
    }

    public ImportedEntityRepresentation getResidenceCountry() {
        return residenceCountry;
    }

    public void setResidenceCountry(ImportedEntityRepresentation residenceCountry) {
        this.residenceCountry = residenceCountry;
    }
}
