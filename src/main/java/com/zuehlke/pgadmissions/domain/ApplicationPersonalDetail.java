package com.zuehlke.pgadmissions.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

@Entity
@Table(name = "APPLICATION_PERSONAL_DETAIL")
public class ApplicationPersonalDetail {

    @Id
    @GeneratedValue
    private Integer id;
    
    @OneToOne(mappedBy = "personalDetail")
    private Application application;

    @Column(name = "skype")
    @Size(min = 6, max = 32)
    private String messenger;

    @Column(name = "phone", nullable = false)
    @Size(max = 50)
    private String phoneNumber;

    @Column(name = "first_language_english", nullable = false)
    private Boolean firstLanguageEnglish;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_language_qualification_id")
    private ApplicationLanguageQualification languageQualification;

    @Column(name = "visa_required", nullable = false)
    private Boolean visaRequired;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_passport_id")
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

    @ManyToOne
    @JoinColumn(name = "gender_id", nullable = false)
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
        return passport != null;
    }

    public ApplicationPassport getPassport() {
        return passport;
    }

    public void setPassport(ApplicationPassport passport) {
        this.passport = passport;
    }

    public Boolean getLanguageQualificationAvailable() {
        return languageQualification != null;
    }

    public ApplicationLanguageQualification getLanguageQualification() {
        return languageQualification;
    }

    public void setLanguageQualification(ApplicationLanguageQualification languageQualification) {
        this.languageQualification = languageQualification;
    }

    public ApplicationPersonalDetail withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationPersonalDetail withApplication(Application application) {
        this.application = application;
        return this;
    }

    public ApplicationPersonalDetail withTitle(Title title) {
        this.title = title;
        return this;
    }

    public ApplicationPersonalDetail withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public ApplicationPersonalDetail withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ApplicationPersonalDetail withCountry(Country country) {
        this.country = country;
        return this;
    }

    public ApplicationPersonalDetail withFirstNationality(Language firstNationality) {
        this.firstNationality = firstNationality;
        return this;
    }

    public ApplicationPersonalDetail withSecondNationality(Language secondNationality) {
        this.secondNationality = secondNationality;
        return this;
    }

    public ApplicationPersonalDetail withEnglishFirstLanguage(Boolean englishFirstLanguage) {
        this.firstLanguageEnglish = englishFirstLanguage;
        return this;
    }

    public ApplicationPersonalDetail withLanguageQualification(ApplicationLanguageQualification languageQualification) {
        this.languageQualification = languageQualification;
        return this;
    }

    public ApplicationPersonalDetail withResidenceCountry(Domicile residenceCountry) {
        this.residenceCountry = residenceCountry;
        return this;
    }

    public ApplicationPersonalDetail withRequiresVisa(Boolean requiresVisa) {
        this.visaRequired = requiresVisa;
        return this;
    }

    public ApplicationPersonalDetail withPassportInformation(ApplicationPassport passportInformation) {
        this.passport = passportInformation;
        return this;
    }

    public ApplicationPersonalDetail withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public ApplicationPersonalDetail withMessenger(String messenger) {
        this.messenger = messenger;
        return this;
    }

    public ApplicationPersonalDetail withEthnicity(Ethnicity eth) {
        ethnicity = eth;
        return this;
    }

    public ApplicationPersonalDetail withDisability(Disability dis) {
        this.disability = dis;
        return this;
    }

}
