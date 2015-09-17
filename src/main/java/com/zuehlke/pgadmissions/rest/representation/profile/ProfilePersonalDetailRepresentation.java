package com.zuehlke.pgadmissions.rest.representation.profile;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSectionRepresentation;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

public class ProfilePersonalDetailRepresentation extends ApplicationSectionRepresentation {

    private ImportedEntityResponse title;

    private ImportedEntityResponse gender;

    private LocalDate dateOfBirth;

    private ImportedEntityResponse ageRange;

    private ImportedEntityResponse nationality;

    private ImportedEntityResponse domicile;

    private Boolean visaRequired;

    private String skype;

    private String phone;

    private ImportedEntityResponse ethnicity;

    private ImportedEntityResponse disability;

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

    public ImportedEntityResponse getNationality() {
        return nationality;
    }

    public void setNationality(ImportedEntityResponse nationality) {
        this.nationality = nationality;
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

    public ImportedEntityResponse getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(ImportedEntityResponse ethnicity) {
        this.ethnicity = ethnicity;
    }

    public ImportedEntityResponse getDisability() {
        return disability;
    }

    public void setDisability(ImportedEntityResponse disability) {
        this.disability = disability;
    }

    public ProfilePersonalDetailRepresentation withTitle(ImportedEntityResponse title) {
        this.title = title;
        return this;
    }

    public ProfilePersonalDetailRepresentation withGender(ImportedEntityResponse gender) {
        this.gender = gender;
        return this;
    }

    public ProfilePersonalDetailRepresentation withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ProfilePersonalDetailRepresentation withAgeRange(ImportedEntityResponse ageRange) {
        this.ageRange = ageRange;
        return this;
    }

    public ProfilePersonalDetailRepresentation withFirstNationality(ImportedEntityResponse nationality) {
        this.nationality = nationality;
        return this;
    }

    public ProfilePersonalDetailRepresentation withDomicile(ImportedEntityResponse domicile) {
        this.domicile = domicile;
        return this;
    }

    public ProfilePersonalDetailRepresentation withVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
        return this;
    }

    public ProfilePersonalDetailRepresentation withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public ProfilePersonalDetailRepresentation withSkype(String skype) {
        this.skype = skype;
        return this;
    }

    public ProfilePersonalDetailRepresentation withEthnicity(ImportedEntityResponse ethnicity) {
        this.ethnicity = ethnicity;
        return this;
    }

    public ProfilePersonalDetailRepresentation withDisability(ImportedEntityResponse disability) {
        this.disability = disability;
        return this;
    }

    public ProfilePersonalDetailRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

}