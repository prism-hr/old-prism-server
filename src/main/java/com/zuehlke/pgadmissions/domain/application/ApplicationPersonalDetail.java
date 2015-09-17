package com.zuehlke.pgadmissions.domain.application;

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
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.profile.ProfilePersonalDetail;

@Entity
@Table(name = "application_personal_detail")
public class ApplicationPersonalDetail extends ApplicationSection implements ProfilePersonalDetail<Application> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "personalDetail")
    private Application association;

    @ManyToOne
    @JoinColumn(name = "imported_title_id")
    private ImportedEntitySimple title;

    @ManyToOne
    @JoinColumn(name = "imported_gender_id")
    private ImportedEntitySimple gender;

    @ManyToOne
    @JoinColumn(name = "imported_age_range_id")
    private ImportedAgeRange ageRange;

    @ManyToOne
    @JoinColumn(name = "imported_domicile_id")
    private ImportedDomicile domicile;

    @ManyToOne
    @JoinColumn(name = "imported_nationality_id")
    private ImportedDomicile nationality;

    @Column(name = "visa_required")
    private Boolean visaRequired;

    @Column(name = "skype")
    @Size(min = 6, max = 32)
    private String skype;

    @Column(name = "phone", nullable = false)
    @Size(max = 50)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "imported_ethnicity_id")
    private ImportedEntitySimple ethnicity;

    @ManyToOne
    @JoinColumn(name = "imported_disability_id")
    private ImportedEntitySimple disability;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public Application getAssociation() {
        return association;
    }

    public void setAssociation(Application association) {
        this.association = association;
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

    public ImportedAgeRange getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(ImportedAgeRange ageRange) {
        this.ageRange = ageRange;
    }

    public ImportedDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedDomicile domicile) {
        this.domicile = domicile;
    }

    public ImportedDomicile getNationality() {
        return nationality;
    }

    public void setNationality(ImportedDomicile nationality) {
        this.nationality = nationality;
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

    public ImportedEntitySimple getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(ImportedEntitySimple ethnicity) {
        this.ethnicity = ethnicity;
    }

    public ImportedEntitySimple getDisability() {
        return disability;
    }

    public void setDisability(ImportedEntitySimple disability) {
        this.disability = disability;
    }

    @Override
    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

}
