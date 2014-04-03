package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;

import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.Title;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPLICATION_FORM_PERSONAL_DETAIL")
public class PersonalDetails implements FormSectionObject, Serializable {

    private static final long serialVersionUID = 6549850558507667533L;

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

    @Column(name = "language_qualification_available")
    private Boolean languageQualificationAvailable;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_language_qualification_id")
    @Valid
    private LanguageQualification languageQualification = null;
    
    @Column(name = "requires_visa")
    private Boolean requiresVisa;

    @Column(name = "passport_available")
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
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

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
    private ApplicationForm application = null;

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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Domicile getResidenceCountry() {
        return residenceCountry;
    }

    public void setResidenceCountry(Domicile residenceCountry) {
        this.residenceCountry = residenceCountry;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
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

}
