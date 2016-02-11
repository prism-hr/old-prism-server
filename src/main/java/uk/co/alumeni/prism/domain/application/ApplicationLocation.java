package uk.co.alumeni.prism.domain.application;

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

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.advert.Advert;

@Entity
@Table(name = "application_location", uniqueConstraints = { @UniqueConstraint(columnNames = { "application_id", "location_advert_id" }) })
public class ApplicationLocation extends ApplicationTagSection<Advert>implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
    private Application association;

    @ManyToOne
    @JoinColumn(name = "location_advert_id", nullable = false)
    private Advert tag;

    @Column(name = "description")
    private String description;

    @Column(name = "description_year")
    private Integer descriptionYear;

    @Column(name = "description_month")
    private Integer descriptionMonth;

    @Column(name = "preference", nullable = false)
    private Boolean preference;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Application getAssociation() {
        return association;
    }

    @Override
    public void setAssociation(Application association) {
        this.association = association;
    }

    @Override
    public Advert getTag() {
        return tag;
    }

    @Override
    public void setTag(Advert tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDescriptionYear() {
        return descriptionYear;
    }

    public void setDescriptionYear(Integer descriptionYear) {
        this.descriptionYear = descriptionYear;
    }

    public Integer getDescriptionMonth() {
        return descriptionMonth;
    }

    public void setDescriptionMonth(Integer descriptionMonth) {
        this.descriptionMonth = descriptionMonth;
    }

    public Boolean getPreference() {
        return preference;
    }

    public void setPreference(Boolean preference) {
        this.preference = preference;
    }

    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

}
