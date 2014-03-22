package com.zuehlke.pgadmissions.domain;

import java.util.AbstractMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

@Entity(name = "STATE")
public class State {
    
    private static int SECONDS_IN_DAY = 86400;
    private static int DAYS_IN_WEEK = 7;
    
	@Id
    @Column(name="id")
	@Enumerated(EnumType.STRING)
	private ApplicationFormStatus id;

	@Column(name="duration")
	private Integer duration;

	@Column(name="can_be_assigned_to")
	private Boolean canBeAssignedTo;
	
	@Column(name="can_be_assigned_from")
	private Boolean canBeAssignedFrom;
	
	@Column(name="is_submitted")
    private Boolean submitted;
   
    @Column(name="is_modifiable")
    private Boolean modifiable;
    
    @Column(name="is_completed")
    private Boolean completed;
    
    @Column(name="is_under_consideration")
    private Boolean underConsideration;
	
	public ApplicationFormStatus getId() {
		return id;
	}
	
	public void setId(ApplicationFormStatus id) {
		this.id = id;
	}
	
	public Integer getDuration() {
		return duration;
	}
	
	public Integer getDurationInDays() {
	    return duration/SECONDS_IN_DAY;
	}
	
    public AbstractMap.SimpleEntry<Integer, DurationUnitEnum> getDisplayDuration() {
        Integer durationInDays = duration/SECONDS_IN_DAY;
        if (durationInDays >= DAYS_IN_WEEK) {
            return new AbstractMap.SimpleEntry<Integer, DurationUnitEnum>(durationInDays/DAYS_IN_WEEK, DurationUnitEnum.WEEKS);
        }
        return new AbstractMap.SimpleEntry<Integer, DurationUnitEnum>(durationInDays, DurationUnitEnum.DAYS);
    }
	
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
    public void setDisplayDuration(Integer duration, DurationUnitEnum unit){
        this.duration = duration * SECONDS_IN_DAY;
        if (unit == DurationUnitEnum.WEEKS) {
            this.duration = this.duration * DAYS_IN_WEEK;
        }
    }
	
    public Boolean getCanBeAssignedTo() {
        return canBeAssignedTo;
    }

    public void setCanBeAssignedTo(Boolean canBeAssignedTo) {
        this.canBeAssignedTo = canBeAssignedTo;
    }

    public Boolean getCanBeAssignedFrom() {
        return canBeAssignedFrom;
    }

    public void setCanBeAssignedFrom(Boolean canBeAssignedFrom) {
        this.canBeAssignedFrom = canBeAssignedFrom;
    }
    
    public Boolean getSubmitted() {
        return submitted;
    }
    
    public Boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(Boolean submitted) {
        this.submitted = submitted;
    }

    public Boolean getModifiable() {
        return modifiable;
    }
    
    public Boolean isModifiable() {
        return modifiable;
    }

    public void setModifiable(Boolean modifiable) {
        this.modifiable = modifiable;
    }
    
    public Boolean getCompleted() {
        return completed;
    }
    
    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getUnderConsideration() {
        return underConsideration;
    }

    public void setUnderConsideration(Boolean underConsideration) {
        this.underConsideration = underConsideration;
    }
    
}
