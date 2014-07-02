package com.zuehlke.pgadmissions.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.Gender;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_PERSONAL_DETAIL")
public class ApplicationPersonalDetails {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "skype")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String messenger;

    @Column(name = "phone", nullable = false)
    @ESAPIConstraint(rule = "PhoneNumber", maxLength = 35, message = "{text.field.notphonenumber}")
    private String phoneNumber;

    @Column(name = "first_language_english", nullable = false)
    private Boolean firstLanguageEnglish;

    @Transient
    private Boolean languageQualificationAvailable;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_language_qualification_id")
    @Valid
    private ApplicationLanguageQualification languageQualification;

    @Column(name = "visa_required", nullable = false)
    private Boolean visaRequired;

    @Transient
    private Boolean passportAvailable;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_passport_id")
    @Valid
    private ApplicationPassport passport;

    @ManyToOne
    @JoinColumn(name = "nationality_id1", nullable = false)
    private Language firstNationality;

    @ManyToOne
    @JoinColumn(name = "nationality_id2")
    private Language secondNationality;

    @ManyToOne
    @JoinColumn(name = "title_id", nullable = false)
    private Title title;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne
    @JoinColumn(name = "ethnicity_id", nullable = false)
    private Ethnicity ethnicity;

    @ManyToOne
    @JoinColumn(name = "disability_id", nullable = false)
    private Disability disability;

    @ManyToOne
    @JoinColumn(name = "domicile_id", nullable = false)
    private Domicile residenceCountry;

    @OneToOne(mappedBy = "personalDetails")
    private Application application = null;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Language getFirstNationality() {
        return firstNationality;
    }

    public void setFirstNationality(Language firstNationality) {
        this.firstNationality = firstNationality;
    }

    public Language getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(Language secondNationality) {
        this.secondNationality = secondNationality;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
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

    public Domicile getResidenceCountry() {
        return residenceCountry;
    }

    public void setResidenceCountry(Domicile residenceCountry) {
        this.residenceCountry = residenceCountry;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public void setEthnicity(Ethnicity eth) {
        this.ethnicity = eth;
    }

    public Ethnicity getEthnicity() {
        return ethnicity;
    }

    public void setDisability(Disability disability) {
        this.disability = disability;
    }

    public Disability getDisability() {
        return disability;
    }

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        if (StringUtils.isBlank(messenger)) {
            this.messenger = null;
        }
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

    public Boolean getVisaRequired() {
        return visaRequired;
    }

    public void setVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
    }

    public Boolean getPassportAvailable() {
        return passportAvailable;
    }

    public void setPassportAvailable(Boolean passportAvailable) {
        this.passportAvailable = passportAvailable;
    }

    public ApplicationPassport getPassport() {
        return passport;
    }

    public void setPassport(ApplicationPassport passport) {
        this.passport = passport;
    }

    public Boolean getLanguageQualificationAvailable() {
        return languageQualificationAvailable;
    }

    public void setLanguageQualificationAvailable(Boolean languageQualificationAvailable) {
        this.languageQualificationAvailable = languageQualificationAvailable;
    }

    public ApplicationLanguageQualification getLanguageQualification() {
        return languageQualification;
    }

    public void setLanguageQualification(ApplicationLanguageQualification languageQualification) {
        this.languageQualification = languageQualification;
    }

    public ApplicationPersonalDetails withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationPersonalDetails withApplication(Application application) {
        this.application = application;
        return this;
    }

    public ApplicationPersonalDetails withTitle(Title title) {
        this.title = title;
        return this;
    }

    public ApplicationPersonalDetails withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public ApplicationPersonalDetails withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ApplicationPersonalDetails withCountry(Country country) {
        this.country = country;
        return this;
    }

    public ApplicationPersonalDetails withFirstNationality(Language firstNationality) {
        this.firstNationality = firstNationality;
        return this;
    }

    public ApplicationPersonalDetails withSecondNationality(Language secondNationality) {
        this.secondNationality = secondNationality;
        return this;
    }

    public ApplicationPersonalDetails withEnglishFirstLanguage(Boolean englishFirstLanguage) {
        this.firstLanguageEnglish = englishFirstLanguage;
        return this;
    }

    public ApplicationPersonalDetails withLanguageQualificationAvailable(Boolean flag) {
        this.languageQualificationAvailable = flag;
        return this;
    }

    public ApplicationPersonalDetails withLanguageQualification(ApplicationLanguageQualification languageQualification) {
        this.languageQualification = languageQualification;
        return this;
    }

    public ApplicationPersonalDetails withResidenceCountry(Domicile residenceCountry) {
        this.residenceCountry = residenceCountry;
        return this;
    }

    public ApplicationPersonalDetails withRequiresVisa(Boolean requiresVisa) {
        this.visaRequired = requiresVisa;
        return this;
    }

    public ApplicationPersonalDetails withPassportAvailable(Boolean passportAvailable) {
        this.passportAvailable = passportAvailable;
        return this;
    }

    public ApplicationPersonalDetails withPassportInformation(ApplicationPassport passportInformation) {
        this.passport = passportInformation;
        return this;
    }

    public ApplicationPersonalDetails withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public ApplicationPersonalDetails withMessenger(String messenger) {
        this.messenger = messenger;
        return this;
    }

    public ApplicationPersonalDetails withEthnicity(Ethnicity eth) {
        ethnicity = eth;
        return this;
    }

    public ApplicationPersonalDetails withDisability(Disability dis) {
        this.disability = dis;
        return this;
    }

}
