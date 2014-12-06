package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

@Entity
@Table(name = "APPLICATION_PRIZE")
public class ApplicationPrize extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
    private Application application;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "award_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate awardDate;
    
    @Column(name = "submitted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final Application getApplication() {
        return application;
    }

    public final void setApplication(Application application) {
        this.application = application;
    }

    public final String getProvider() {
        return provider;
    }

    public final void setProvider(String provider) {
        this.provider = provider;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final LocalDate getAwardDate() {
        return awardDate;
    }

    public final void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }
    
    @Override
    public DateTime getLastEditedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastEditedTimestamp(DateTime lastEditedTimestamp) {
        this.lastUpdatedTimestamp = lastEditedTimestamp;
    }
    
    public ApplicationPrize withProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public ApplicationPrize withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public ApplicationPrize withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public ApplicationPrize withAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
        return this;
    }
    
    public String getAwardDateDisplay(String dateFormat) {
        return awardDate.toString(dateFormat);
    }
    
}
