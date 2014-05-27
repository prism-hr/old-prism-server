package com.zuehlke.pgadmissions.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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

import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.Title;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_FORM_PERSONAL_DETAIL")
public class PersonalDetails {

    @Id
    @GeneratedValue
    private Integer id;

    @Transient
    private boolean acceptedTerms;

    @Column(name = "skype")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String messenger;

    @Column(name = "phone")
    @ESAPIConstraint(rule = "PhoneNumber", maxLength = 35, message = "{text.field.notphonenumber}")
    private String phoneNumber;

    @Column(name = "english_first_language")
    private Boolean englishFirstLanguage;

    @Transient
    private Boolean languageQualificationAvailable;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_language_qualification_id")
    @Valid
    private LanguageQualification languageQualification = null;

    @Column(name = "requires_visa")
    private Boolean requiresVisa;

    @Transient
    private Boolean passportAvailable;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_passport")
    @Valid
    private Passport passport = null;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "first_nationality")
    private Language firstNationality;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "second_nationality")
    private Language secondNationality = null;

    @Column(name = "title")
    @Enumerated(EnumType.STRING)
    private Title title;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "date_of_birth")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ethnicity_id")
    private Ethnicity ethnicity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disability_id")
    private Disability disability;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "domicile_id")
    private Domicile residenceCountry;

    @OneToOne(mappedBy = "personalDetails", fetch = FetchType.LAZY)
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

    public Boolean getEnglishFirstLanguage() {
        return englishFirstLanguage;
    }

    public void setEnglishFirstLanguage(Boolean englishFirstLanguage) {
        this.englishFirstLanguage = englishFirstLanguage;
    }

    public Boolean getRequiresVisa() {
        return requiresVisa;
    }

    public void setRequiresVisa(Boolean requiresVisa) {
        this.requiresVisa = requiresVisa;
    }

    public Boolean getPassportAvailable() {
        return passportAvailable;
    }

    public void setPassportAvailable(Boolean passportAvailable) {
        this.passportAvailable = passportAvailable;
    }

    public Passport getPassport() {
        return passport;
    }

    public void setPassport(Passport passport) {
        this.passport = passport;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public Boolean getLanguageQualificationAvailable() {
        return languageQualificationAvailable;
    }

    public void setLanguageQualificationAvailable(Boolean languageQualificationAvailable) {
        this.languageQualificationAvailable = languageQualificationAvailable;
    }

    public LanguageQualification getLanguageQualification() {
        return languageQualification;
    }

    public void setLanguageQualification(LanguageQualification languageQualification) {
        this.languageQualification = languageQualification;
    }

    public PersonalDetails withId(Integer id) {
        this.id = id;
        return this;
    }

    public PersonalDetails withApplication(Application application) {
        this.application = application;
        return this;
    }

    public PersonalDetails withTitle(Title title) {
        this.title = title;
        return this;
    }

    public PersonalDetails withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public PersonalDetails withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public PersonalDetails withCountry(Country country) {
        this.country = country;
        return this;
    }

    public PersonalDetails withFirstNationality(Language firstNationality) {
        this.firstNationality = firstNationality;
        return this;
    }

    public PersonalDetails withSecondNationality(Language secondNationality) {
        this.secondNationality = secondNationality;
        return this;
    }

    public PersonalDetails withEnglishFirstLanguage(Boolean englishFirstLanguage) {
        this.englishFirstLanguage = englishFirstLanguage;
        return this;
    }

    public PersonalDetails withLanguageQualificationAvailable(Boolean flag) {
        this.languageQualificationAvailable = flag;
        return this;
    }

    public PersonalDetails withLanguageQualification(LanguageQualification languageQualification) {
        this.languageQualification = languageQualification;
        return this;
    }

    public PersonalDetails withResidenceCountry(Domicile residenceCountry) {
        this.residenceCountry = residenceCountry;
        return this;
    }

    public PersonalDetails withRequiresVisa(Boolean requiresVisa) {
        this.requiresVisa = requiresVisa;
        return this;
    }

    public PersonalDetails withPassportAvailable(Boolean passportAvailable) {
        this.passportAvailable = passportAvailable;
        return this;
    }

    public PersonalDetails withPassportInformation(Passport passportInformation) {
        this.passport = passportInformation;
        return this;
    }

    public PersonalDetails withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public PersonalDetails withMessenger(String messenger) {
        this.messenger = messenger;
        return this;
    }

    public PersonalDetails withEthnicity(Ethnicity eth) {
        ethnicity = eth;
        return this;
    }

    public PersonalDetails withDisability(Disability dis) {
        this.disability = dis;
        return this;
    }

}
