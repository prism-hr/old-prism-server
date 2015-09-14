package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

@Entity
@Table(name = "application_personal_detail")
public class ApplicationPersonalDetail extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "personalDetail")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "imported_title_id")
    private ImportedEntitySimple title;

    @ManyToOne
    @JoinColumn(name = "imported_gender_id")
    private ImportedEntitySimple gender;

    @Column(name = "date_of_birth", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "imported_age_range_id")
    private ImportedAgeRange ageRange;

    @ManyToOne
    @JoinColumn(name = "imported_country_id")
    private ImportedEntitySimple country;

    @ManyToOne
    @JoinColumn(name = "imported_domicile_id")
    private ImportedEntitySimple domicile;

    @ManyToOne
    @JoinColumn(name = "imported_nationality_id")
    private ImportedEntitySimple firstNationality;

    @Column(name = "first_language_locale")
    private Boolean firstLanguageLocale;

    @Column(name = "visa_required")
    private Boolean visaRequired;

    @Column(name = "skype")
    @Size(min = 6, max = 32)
    private String skype;

    @Column(name = "phone", nullable = false)
    @Size(max = 50)
    private String phone;

    @Embedded
    private ApplicationDemographic demographic;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ImportedEntitySimple getTitle() {
        return title;
    }

    public void setTitle(ImportedEntitySimple title) {
        this.title = title;
    }

    public ImportedEntitySimple getGender() {
        return gender;
    }

    public void setGender(ImportedEntitySimple gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public ImportedAgeRange getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(ImportedAgeRange ageRange) {
        this.ageRange = ageRange;
    }

    public ImportedEntitySimple getCountry() {
        return country;
    }

    public void setCountry(ImportedEntitySimple country) {
        this.country = country;
    }

    public ImportedEntitySimple getFirstNationality() {
        return firstNationality;
    }

    public void setFirstNationality(ImportedEntitySimple firstNationality) {
        this.firstNationality = firstNationality;
    }

    public Boolean getFirstLanguageLocale() {
        return firstLanguageLocale;
    }

    public void setFirstLanguageLocale(Boolean firstLanguageLocale) {
        this.firstLanguageLocale = firstLanguageLocale;
    }

    public ImportedEntitySimple getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedEntitySimple domicile) {
        this.domicile = domicile;
    }

    public Boolean getVisaRequired() {
        return visaRequired;
    }

    public void setVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
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

    public ApplicationDemographic getDemographic() {
        return demographic;
    }

    public void setDemographic(ApplicationDemographic demographic) {
        this.demographic = demographic;
    }

    @Override
    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public ApplicationPersonalDetail withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationPersonalDetail withApplication(Application application) {
        this.application = application;
        return this;
    }

}
