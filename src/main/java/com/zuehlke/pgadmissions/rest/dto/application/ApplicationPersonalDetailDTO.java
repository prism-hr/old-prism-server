package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;

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
    private ImportedEntityDTO firstNationality;

    private ImportedEntityDTO secondNationality;

    private ImportedEntityDTO title;

    @NotNull
    private ImportedEntityDTO gender;

    @NotNull
    @DatePast
    private LocalDate dateOfBirth;

    @NotNull
    private ImportedEntityDTO country;

    private ImportedEntityDTO domicile;

    @Size(min = 1)
    private String studyLocation;

    @Size(min = 1)
    private String studyDivision;

    @Size(min = 1)
    private String studyArea;

    private ApplicationDemographicDTO demographic;

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

    public ImportedEntityDTO getFirstNationality() {
        return firstNationality;
    }

    public void setFirstNationality(ImportedEntityDTO firstNationality) {
        this.firstNationality = firstNationality;
    }

    public ImportedEntityDTO getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(ImportedEntityDTO secondNationality) {
        this.secondNationality = secondNationality;
    }

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

    public ImportedEntityDTO getCountry() {
        return country;
    }

    public void setCountry(ImportedEntityDTO country) {
        this.country = country;
    }

    public ImportedEntityDTO getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedEntityDTO domicile) {
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

    public ApplicationDemographicDTO getDemographic() {
        return demographic;
    }

    public void setDemographic(ApplicationDemographicDTO demographic) {
        this.demographic = demographic;
    }
}
