package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.enums.AdvertType;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "ADVERT")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Advert implements Serializable {
    private static final long serialVersionUID = 5963260213501162814L;

    @Id
    @GeneratedValue
    private Integer id;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "title")
    private String title;
    
    @Size(max = 3000, message = "A maximum of 2000 characters are allowed.")
    @Column(name = "description", nullable = false)
    private String description = "Advert coming soon!";

    @Column(name = "study_duration")
    private Integer studyDuration;

    @Size(max = 2000, message = "A maximum of 1000 characters are allowed.")
    @Column(name = "funding")
    private String funding;

    @Column(name = "active")
    private Boolean active = true;
    
    @Column(name = "enabled")
    private Boolean enabled = true;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_user_id")
    private RegisteredUser contactUser;
    
    @Column(name = "advert_type")
    @Enumerated(EnumType.STRING)
    private AdvertType advertType;
    
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

    public RegisteredUser getContactUser() {
        return contactUser;
    }

    public void setContactUser(RegisteredUser contactUser) {
        this.contactUser = contactUser;
    }
    
    public AdvertType getAdvertType() {
        return advertType;
    }
    
    public void setAdvertType(AdvertType advertType) {
        this.advertType = advertType;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getEnabled() {
        return enabled;
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

    public abstract Program getProgram();
    
    public abstract Project getProject();
    
}
