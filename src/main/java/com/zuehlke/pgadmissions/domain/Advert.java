package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.google.common.base.Objects;

@Entity
@Table(name = "ADVERT")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Advert extends PrismResourceTransient {

    @Id
    @GeneratedValue
    private Integer id;

    @Size(max = 3000, message = "A maximum of 2000 characters are allowed.")
    @Column(name = "description", nullable = false)
    private String description = "Advert coming soon!";

    @Column(name = "study_duration")
    private Integer studyDuration;

    @Size(max = 2000, message = "A maximum of 1000 characters are allowed.")
    @Column(name = "funding")
    private String funding;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "advert_closing_date_id")
    private AdvertClosingDate closingDate;
    
    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private List<AdvertClosingDate> closingDates = new ArrayList<AdvertClosingDate>();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStudyDuration() {
        return studyDuration;
    }

    public void setStudyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public String getDescriptionForFacebook() {
        return getStudyDurationToRead().toLowerCase().replace("s", "")
                + " research study programme delivered by UCL Engineering at London's global University. "
                + "Click to find out more about the programme and apply for your place.";
    }

    public String getStudyDurationToRead() {
        Integer studyDurationToRead = studyDuration;
        String timeIntervalToRead = "Month";

        if (studyDuration % 12 == 0) {
            studyDurationToRead = studyDuration / 12;
            timeIntervalToRead = "Year";
        }

        if (studyDurationToRead > 1) {
            timeIntervalToRead = timeIntervalToRead + "s";
        }

        return studyDurationToRead.toString() + " " + timeIntervalToRead;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AdvertClosingDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(AdvertClosingDate closingDate) {
        this.closingDate = closingDate;
    }

    public List<AdvertClosingDate> getClosingDates() {
        return closingDates;
    }
    
    @Override
    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Advert other = (Advert) obj;
        return Objects.equal(id, other.getId());
    }

    public abstract String getTitle();
    
    public abstract void setTitle(String title);

}
