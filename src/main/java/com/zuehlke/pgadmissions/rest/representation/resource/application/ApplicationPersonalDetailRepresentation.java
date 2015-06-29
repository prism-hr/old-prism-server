package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;

public class ApplicationPersonalDetailRepresentation extends ApplicationSectionRepresentation {

    private String skype;

    private String phone;

    private Boolean firstLanguageLocale;

    private ApplicationLanguageQualificationRepresentation languageQualification;

    private Boolean visaRequired;

    private ApplicationPassportRepresentation passport;

    private ImportedEntitySimpleRepresentation firstNationalityMapping;
    
    private ImportedEntitySimpleRepresentation secondNationalityMapping;
    
    private ImportedEntitySimpleRepresentation titleMapping;
    
    private ImportedEntitySimpleRepresentation genderMapping;

    private LocalDate dateOfBirth;
    
    private ImportedEntitySimpleRepresentation countryMapping;

    private ImportedEntitySimpleRepresentation ethnicityMapping;

    private ImportedEntitySimpleRepresentation disabilityMapping;

    private ImportedEntitySimpleRepresentation domicileMapping;

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

    public ApplicationLanguageQualificationRepresentation getLanguageQualification() {
        return languageQualification;
    }

    public void setLanguageQualification(ApplicationLanguageQualificationRepresentation languageQualification) {
        this.languageQualification = languageQualification;
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

    public Integer getFirstNationality() {
        return firstNationalityMapping.getId();
    }
    
    public void setFirstNationality(Integer firstNationality) {
        this.firstNationalityMapping = new ImportedEntitySimpleRepresentation().withId(firstNationality);
    }

    public ImportedEntitySimpleRepresentation getFirstNationalityMapping() {
        return firstNationalityMapping;
    }

    public void setFirstNationalityMapping(ImportedEntitySimpleRepresentation firstNationalityMapping) {
        this.firstNationalityMapping = firstNationalityMapping;
    }

    public Integer getSecondNationality() {
        return secondNationalityMapping.getId();
    }
    
    public void setSecondNationality(Integer secondNationality) {
        this.secondNationalityMapping = new ImportedEntitySimpleRepresentation().withId(secondNationality);
    }

    public ImportedEntitySimpleRepresentation getSecondNationalityMapping() {
        return secondNationalityMapping;
    }

    public void setSecondNationalityMapping(ImportedEntitySimpleRepresentation secondNationalityMapping) {
        this.secondNationalityMapping = secondNationalityMapping;
    }

    public Integer getTitle() {
        return titleMapping.getId();
    }
    
    public void setTitle(Integer title) {
        this.titleMapping = new ImportedEntitySimpleRepresentation().withId(title);
    }

    public ImportedEntitySimpleRepresentation getTitleMapping() {
        return titleMapping;
    }

    public void setTitleMapping(ImportedEntitySimpleRepresentation titleMapping) {
        this.titleMapping = titleMapping;
    }

    public Integer getGender() {
        return genderMapping.getId();
    }
    
    public void setGender(Integer gender) {
        this.genderMapping = new ImportedEntitySimpleRepresentation().withId(gender);
    }

    public ImportedEntitySimpleRepresentation getGenderMapping() {
        return genderMapping;
    }

    public void setGenderMapping(ImportedEntitySimpleRepresentation genderMapping) {
        this.genderMapping = genderMapping;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getCountry() {
        return countryMapping.getId();
    }
    
    public void setCountry(Integer country) {
        this.countryMapping = new ImportedEntitySimpleRepresentation().withId(country);
    }

    public ImportedEntitySimpleRepresentation getCountryMapping() {
        return countryMapping;
    }

    public void setCountryMapping(ImportedEntitySimpleRepresentation countryMapping) {
        this.countryMapping = countryMapping;
    }

    public Integer getEthnicity() {
        return ethnicityMapping.getId();
    }

    public void setEthnicity(Integer ethnicity) {
        this.ethnicityMapping = new ImportedEntitySimpleRepresentation().withId(ethnicity);
    }

    public ImportedEntitySimpleRepresentation getEthnicityMapping() {
        return ethnicityMapping;
    }

    public void setEthnicityMapping(ImportedEntitySimpleRepresentation ethnicityMapping) {
        this.ethnicityMapping = ethnicityMapping;
    }

    public Integer getDisability() {
        return disabilityMapping.getId();
    }

    public void setDisability(Integer disability) {
        this.disabilityMapping = new ImportedEntitySimpleRepresentation().withId(disability);
    }
    
    public ImportedEntitySimpleRepresentation getDisabilityMapping() {
        return disabilityMapping;
    }

    public void setDisabilityMapping(ImportedEntitySimpleRepresentation disabilityMapping) {
        this.disabilityMapping = disabilityMapping;
    }

    public Integer getDomicile() {
        return domicileMapping.getId();
    }

    public void setDomicile(Integer domicile) {
        this.domicileMapping = new ImportedEntitySimpleRepresentation().withId(domicile);
    }

    public ImportedEntitySimpleRepresentation getDomicileMapping() {
        return domicileMapping;
    }

    public void setDomicileMapping(ImportedEntitySimpleRepresentation domicileMapping) {
        this.domicileMapping = domicileMapping;
    }
    
}
