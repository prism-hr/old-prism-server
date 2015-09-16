package com.zuehlke.pgadmissions.domain.application;

import static com.zuehlke.pgadmissions.PrismConstants.BACK_SLASH;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.profile.ProfileEmploymentPosition;

@Entity
@Table(name = "application_employment_position", uniqueConstraints = { @UniqueConstraint(columnNames = { "application_id", "advert_id", "start_year" }) })
public class ApplicationEmploymentPosition extends ApplicationAdvertRelationSection implements ProfileEmploymentPosition<Application> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
    private Application association;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "start_month", nullable = false)
    private Integer startMonth;

    @Column(name = "end_year")
    private Integer endYear;

    @Column(name = "end_month")
    private Integer endMonth;

    @Column(name = "current", nullable = false)
    private Boolean current;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Application getAssociation() {
        return association;
    }

    public void setAssociation(Application association) {
        this.association = association;
    }

    @Override
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public Integer getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(Integer endMonth) {
        this.endMonth = endMonth;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    @Override
    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public String getStartDateDisplay() {
        return startYear == null ? null : startMonth.toString() + BACK_SLASH + startYear.toString();
    }

    public String getEndDateDisplay() {
        return endYear == null ? null : endMonth.toString() + BACK_SLASH + endYear.toString();
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("startYear", startYear);
    }

}
