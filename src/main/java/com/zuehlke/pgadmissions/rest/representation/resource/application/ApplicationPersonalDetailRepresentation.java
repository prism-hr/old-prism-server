package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;

public class ApplicationPersonalDetailRepresentation extends ApplicationSectionRepresentation {

    private ImportedEntitySimpleRepresentation title;

    private ImportedEntitySimpleRepresentation gender;

    private LocalDate dateOfBirth;

    private ImportedEntitySimpleRepresentation ageRange;

    private ImportedEntitySimpleRepresentation firstNationality;

    private ImportedEntitySimpleRepresentation secondNationality;

    private ImportedEntitySimpleRepresentation country;

    private Boolean firstLanguageLocale;

    private ApplicationLanguageQualificationRepresentation languageQualification;

    private ImportedEntitySimpleRepresentation domicile;

    private Boolean visaRequired;

    private ApplicationPassportRepresentation passport;

    private String skype;

    private String phone;

    private ImportedEntitySimpleRepresentation ethnicity;

    private ImportedEntitySimpleRepresentation disability;

    public ImportedEntitySimpleRepresentation getTitle() {
        return title;
    }

    public void setTitle(ImportedEntitySimpleRepresentation title) {
        this.title = title;
    }

    public ImportedEntitySimpleRepresentation getGender() {
        return gender;
    }

    public void setGender(ImportedEntitySimpleRepresentation gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public ImportedEntitySimpleRepresentation getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(ImportedEntitySimpleRepresentation ageRange) {
        this.ageRange = ageRange;
    }

    public ImportedEntitySimpleRepresentation getFirstNationality() {
        return firstNationality;
    }

    public void setFirstNationality(ImportedEntitySimpleRepresentation firstNationality) {
        this.firstNationality = firstNationality;
    }

    public ImportedEntitySimpleRepresentation getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(ImportedEntitySimpleRepresentation secondNationality) {
        this.secondNationality = secondNationality;
    }

    public ImportedEntitySimpleRepresentation getCountry() {
        return country;
    }

    public void setCountry(ImportedEntitySimpleRepresentation country) {
        this.country = country;
    }

    public Boolean getFirstLanguageLocale() {
        return firstLanguageLocale;
    }

    public void setFirstLanguageLocale(Boolean firstLanguageLocale) {
        this.firstLanguageLocale = firstLanguageLocale;
    }

    public ApplicationLanguageQualificationRepresentation getLanguageQualification() {
        return languageQualification;
    }

    public void setLanguageQualification(ApplicationLanguageQualificationRepresentation languageQualification) {
        this.languageQualification = languageQualification;
    }

    public ImportedEntitySimpleRepresentation getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedEntitySimpleRepresentation domicile) {
        this.domicile = domicile;
    }

    public Boolean getVisaRequired() {
        return visaRequired;
    }

    public void setVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
    }

    public ApplicationPassportRepresentation getPassport() {
        return passport;
    }

    public void setPassport(ApplicationPassportRepresentation passport) {
        this.passport = passport;
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

    public ImportedEntitySimpleRepresentation getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(ImportedEntitySimpleRepresentation ethnicity) {
        this.ethnicity = ethnicity;
    }

    public ImportedEntitySimpleRepresentation getDisability() {
        return disability;
    }

    public void setDisability(ImportedEntitySimpleRepresentation disability) {
        this.disability = disability;
    }

    public ApplicationPersonalDetailRepresentation withTitle(ImportedEntitySimpleRepresentation title) {
        this.title = title;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withGender(ImportedEntitySimpleRepresentation gender) {
        this.gender = gender;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withAgeRange(ImportedEntitySimpleRepresentation ageRange) {
        this.ageRange = ageRange;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withFirstNationality(ImportedEntitySimpleRepresentation firstNationality) {
        this.firstNationality = firstNationality;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withSecondNationality(ImportedEntitySimpleRepresentation secondNationality) {
        this.secondNationality = secondNationality;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withCountry(ImportedEntitySimpleRepresentation country) {
        this.country = country;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withFirstLanguageLocale(Boolean firstLanguageLocale) {
        this.firstLanguageLocale = firstLanguageLocale;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withLanguageQualification(ApplicationLanguageQualificationRepresentation languageQualification) {
        this.languageQualification = languageQualification;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withDomicile(ImportedEntitySimpleRepresentation domicile) {
        this.domicile = domicile;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withPassport(ApplicationPassportRepresentation passport) {
        this.passport = passport;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withSkype(String skype) {
        this.skype = skype;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withEthnicity(ImportedEntitySimpleRepresentation ethnicity) {
        this.ethnicity = ethnicity;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withDisability(ImportedEntitySimpleRepresentation disability) {
        this.disability = disability;
        return this;
    }

}
