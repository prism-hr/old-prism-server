package com.zuehlke.pgadmissions.domain.application;

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

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.domain.imported.Country;
import com.zuehlke.pgadmissions.domain.imported.Disability;
import com.zuehlke.pgadmissions.domain.imported.Domicile;
import com.zuehlke.pgadmissions.domain.imported.Ethnicity;
import com.zuehlke.pgadmissions.domain.imported.Gender;
import com.zuehlke.pgadmissions.domain.imported.Language;
import com.zuehlke.pgadmissions.domain.imported.Title;

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
    private String skype;

    @Column(name = "phone", nullable = false)
    @Size(max = 50)
    private String phone;

    @Column(name = "first_language_locale", nullable = false)
    private Boolean firstLanguageLocale;

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
    private Domicile domicile;

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

    public Domicile getDomicile() {
        return domicile;
    }

    public void setDomicile(Domicile domicile) {
        this.domicile = domicile;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public Boolean getFirstLanguageLocale() {
        return firstLanguageLocale;
    }

    public void setFirstLanguageLocale(Boolean firstLanguageLocale) {
        this.firstLanguageLocale = firstLanguageLocale;
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

    public ApplicationPersonalDetail withFirstLanguageLocale(Boolean firstLanguageLocale) {
        this.firstLanguageLocale = firstLanguageLocale;
        return this;
    }

    public ApplicationPersonalDetail withLanguageQualification(ApplicationLanguageQualification languageQualification) {
        this.languageQualification = languageQualification;
        return this;
    }

    public ApplicationPersonalDetail withResidenceCountry(Domicile residenceCountry) {
        this.domicile = residenceCountry;
        return this;
    }

    public ApplicationPersonalDetail withRequiresVisa(Boolean requiresVisa) {
        this.visaRequired = requiresVisa;
        return this;
    }

    public ApplicationPersonalDetail withPassport(ApplicationPassport passport) {
        this.passport = passport;
        return this;
    }

    public ApplicationPersonalDetail withPhone(String phoneNumber) {
        this.phone = phoneNumber;
        return this;
    }

    public ApplicationPersonalDetail withSkype(String skype) {
        this.skype = skype;
        return this;
    }

    public ApplicationPersonalDetail withEthnicity(Ethnicity ethnicity) {
        this.ethnicity = ethnicity;
        return this;
    }

    public ApplicationPersonalDetail withDisability(Disability disability) {
        this.disability = disability;
        return this;
    }
    
    public String getDateOfBirth(String dateFormat) {
        return dateOfBirth == null ? null : dateOfBirth.toString(dateFormat);
    }
    
    public String getTitleDisplay() {
        return title == null ? null : title.getName();
    }
    
    public String getGenderDisplay() {
        return gender == null ? null : gender.getName();
    }
    
    public String getCountryDisplay() {
        return country == null ? null : country.getName();
    }
    
    public String getDomicileDisplay() {
        return domicile == null ? null : domicile.getName();
    }
    
    public String getEthnicityDisplay() {
        return ethnicity == null ? null : ethnicity.getName();
    }
    
    public String getDisabilityDisplay() {
        return disability == null ? null : disability.getName();
    } 
    
    public String getNationalityDisplay() {
        String first = firstNationality == null ? null : firstNationality.getName();
        String second = secondNationality == null ? null : secondNationality.getName();
        return Joiner.on(", ").skipNulls().join(first, second);
    }

}
