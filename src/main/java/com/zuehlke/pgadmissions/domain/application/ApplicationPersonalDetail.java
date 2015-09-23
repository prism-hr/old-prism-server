package com.zuehlke.pgadmissions.domain.application;

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
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.AgeRange;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.definitions.PrismGender;
import com.zuehlke.pgadmissions.domain.profile.ProfilePersonalDetail;

@Entity
@Table(name = "application_personal_detail")
public class ApplicationPersonalDetail extends ApplicationSection implements ProfilePersonalDetail<Application> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "personalDetail")
    private Application association;

    @Column(name = "gender_id")
    @Enumerated(EnumType.STRING)
    private PrismGender gender;

    @ManyToOne
    @JoinColumn(name = "age_range_id")
    private AgeRange ageRange;

    @ManyToOne
    @JoinColumn(name = "nationality_id")
    private Domicile nationality;

    @ManyToOne
    @JoinColumn(name = "domicile_id")
    private Domicile domicile;

    @Column(name = "visa_required")
    private Boolean visaRequired;

    @Column(name = "skype")
    @Size(min = 6, max = 32)
    private String skype;

    @Column(name = "phone", nullable = false)
    @Size(max = 50)
    private String phone;

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

    public PrismGender getGender() {
        return gender;
    }

    public void setGender(PrismGender gender) {
        this.gender = gender;
    }

    public AgeRange getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(AgeRange ageRange) {
        this.ageRange = ageRange;
    }

    public Domicile getNationality() {
        return nationality;
    }

    public void setNationality(Domicile nationality) {
        this.nationality = nationality;
    }

    public Domicile getDomicile() {
        return domicile;
    }

    public void setDomicile(Domicile domicile) {
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

    @Override
    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

}
