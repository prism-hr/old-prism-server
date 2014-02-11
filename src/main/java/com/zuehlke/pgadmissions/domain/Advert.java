package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "ADVERT")
@Inheritance(strategy = InheritanceType.JOINED) 
public class Advert implements Serializable {
    private static final long serialVersionUID = 5963260213501162814L;

    @Id
    @GeneratedValue
    private Integer id;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "title")
    private String title;
    
    @Size(max = 3000, message = "A maximum of 2000 characters are allowed.")
    @Column(name = "description", nullable = false)
    private String description = "Programme advert coming soon!";

    @Column(name = "study_duration")
    private Integer studyDuration;

    @Size(max = 2000, message = "A maximum of 1000 characters are allowed.")
    @Column(name = "funding")
    private String funding;

    @Column(name = "active")
    private Boolean active = true;
    
    @Column(name = "enabled")
    private Boolean enabled = true;
    
    @Column(name = "last_edited_timestamp", insertable = true)
    @Generated(GenerationTime.ALWAYS)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastEditedTimestamp;
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "registered_user_id")
    private RegisteredUser contactUser;
    
    @Transient
    private Integer ranking = 0;
    
    @Transient
    private Boolean selected = false;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public Boolean isEnabled() {
        return enabled;
    }
    
    public Boolean isAcceptingApplications() {
        return isActive() && isEnabled();
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescriptionForFacebook() {
    	return getStudyDurationToRead().toLowerCase().replace("s", "") + " research study programme delivered by UCL Engineering at London's global University. " +
    			"Click to find out more about the programme and apply for your place.";
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

    public Date getLastEditedTimestamp() {
        return lastEditedTimestamp;
    }

    public void setLastEditedTimestamp(Date lastEditedTimestamp) {
        this.lastEditedTimestamp = lastEditedTimestamp;
    }
    
    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }
    
    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public RegisteredUser getContactUser() {
        return contactUser;
    }

    public void setContactUser(RegisteredUser contactUser) {
        this.contactUser = contactUser;
    }
    
}
