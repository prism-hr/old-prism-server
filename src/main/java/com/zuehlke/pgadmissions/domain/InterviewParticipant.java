package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "INTERVIEW_PARTICIPANT")
public class InterviewParticipant implements Serializable {

    private static final long serialVersionUID = -730673777949846236L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private RegisteredUser user;

    @Column(name = "responded")
    private Boolean responded = false;

    @ManyToMany
    @JoinTable(name = "INTERVIEW_TIMESLOT_VOTE", joinColumns = { @JoinColumn(name = "participant_id") }, inverseJoinColumns = { @JoinColumn(name = "timeslot_id") })
    private Set<InterviewTimeslot> acceptedTimeslots = new HashSet<InterviewTimeslot>();
    
    @Transient
    private Boolean canMakeIt = false;
    
    @Column(name = "last_notified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastNotified;

    @ManyToOne
    @JoinColumn(name = "interview_id", updatable = false, insertable = false)
    private Interview interview;    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RegisteredUser getUser() {
        return user;
    }

    public void setUser(RegisteredUser user) {
        this.user = user;
    }

    public Boolean getResponded() {
        return responded;
    }

    public void setResponded(Boolean responded) {
        this.responded = responded;
    }

    public Set<InterviewTimeslot> getAcceptedTimeslots() {
        return acceptedTimeslots;
    }

    public void setAcceptedTimeslots(Set<InterviewTimeslot> acceptedTimeslots) {
        this.acceptedTimeslots = acceptedTimeslots;
    }
    
    public Boolean getCanMakeIt() {
        return canMakeIt;
    }
    
    public void setCanMakeIt(Boolean canMakeIt) {
        this.canMakeIt = canMakeIt;
    }
    
    public Date getLastNotified() {
        return lastNotified;
    }

    public void setLastNotified(Date lastNotified) {
        this.lastNotified = lastNotified;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }
}
