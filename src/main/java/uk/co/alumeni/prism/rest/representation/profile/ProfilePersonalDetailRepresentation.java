package uk.co.alumeni.prism.rest.representation.profile;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.domain.definitions.PrismDomicile;
import uk.co.alumeni.prism.domain.definitions.PrismGender;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationSectionRepresentation;

public class ProfilePersonalDetailRepresentation extends ApplicationSectionRepresentation {

    private PrismGender gender;

    private LocalDate dateOfBirth;

    private PrismDomicile nationality;

    private PrismDomicile domicile;

    private Boolean visaRequired;

    private String skype;

    private String phone;

    public PrismGender getGender() {
        return gender;
    }

    public void setGender(PrismGender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public PrismDomicile getNationality() {
        return nationality;
    }

    public void setNationality(PrismDomicile nationality) {
        this.nationality = nationality;
    }

    public PrismDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(PrismDomicile domicile) {
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

    public ProfilePersonalDetailRepresentation withGender(PrismGender gender) {
        this.gender = gender;
        return this;
    }

    public ProfilePersonalDetailRepresentation withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ProfilePersonalDetailRepresentation withNationality(PrismDomicile nationality) {
        this.nationality = nationality;
        return this;
    }

    public ProfilePersonalDetailRepresentation withDomicile(PrismDomicile domicile) {
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

    public ProfilePersonalDetailRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

}
