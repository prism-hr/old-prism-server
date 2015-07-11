package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

public class ApplicationPersonalDetailRepresentation extends ApplicationSectionRepresentation {

    private ImportedEntityResponse title;

    private ImportedEntityResponse gender;

    private LocalDate dateOfBirth;

    private ImportedEntityResponse ageRange;

    private ImportedEntityResponse firstNationality;

    private ImportedEntityResponse secondNationality;

    private ImportedEntityResponse country;

    private Boolean firstLanguageLocale;

    private ApplicationLanguageQualificationRepresentation languageQualification;

    private ImportedEntityResponse domicile;

    private Boolean visaRequired;

    private ApplicationPassportRepresentation passport;

    private String skype;

    private String phone;

    private ApplicationDemographicRepresentation demographic;

    public ImportedEntityResponse getTitle() {
        return title;
    }

    public void setTitle(ImportedEntityResponse title) {
        this.title = title;
    }

    public ImportedEntityResponse getGender() {
        return gender;
    }

    public void setGender(ImportedEntityResponse gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public ImportedEntityResponse getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(ImportedEntityResponse ageRange) {
        this.ageRange = ageRange;
    }

    public ImportedEntityResponse getFirstNationality() {
        return firstNationality;
    }

    public void setFirstNationality(ImportedEntityResponse firstNationality) {
        this.firstNationality = firstNationality;
    }

    public ImportedEntityResponse getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(ImportedEntityResponse secondNationality) {
        this.secondNationality = secondNationality;
    }

    public ImportedEntityResponse getCountry() {
        return country;
    }

    public void setCountry(ImportedEntityResponse country) {
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

    public ImportedEntityResponse getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedEntityResponse domicile) {
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

    public ApplicationDemographicRepresentation getDemographic() {
        return demographic;
    }

    public void setDemographic(ApplicationDemographicRepresentation demographic) {
        this.demographic = demographic;
    }

    public ApplicationPersonalDetailRepresentation withTitle(ImportedEntityResponse title) {
        this.title = title;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withGender(ImportedEntityResponse gender) {
        this.gender = gender;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withAgeRange(ImportedEntityResponse ageRange) {
        this.ageRange = ageRange;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withFirstNationality(ImportedEntityResponse firstNationality) {
        this.firstNationality = firstNationality;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withSecondNationality(ImportedEntityResponse secondNationality) {
        this.secondNationality = secondNationality;
        return this;
    }

    public ApplicationPersonalDetailRepresentation withCountry(ImportedEntityResponse country) {
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

    public ApplicationPersonalDetailRepresentation withDomicile(ImportedEntityResponse domicile) {
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

}
