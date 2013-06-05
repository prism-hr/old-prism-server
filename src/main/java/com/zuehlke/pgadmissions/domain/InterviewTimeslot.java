package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "INTERVIEW_TIMESLOT")
public class InterviewTimeslot implements Serializable {

    private static final long serialVersionUID = -730673777949846236L;

    @Id
    @GeneratedValue
    private Integer id;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "start_time")
    private String startTime;

    @ManyToMany(mappedBy = "acceptedTimeslots")
    private Set<InterviewParticipant> acceptedParticipants = new HashSet<InterviewParticipant>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Set<InterviewParticipant> getAcceptedParticipants() {
        return acceptedParticipants;
    }

    public void setAcceptedParticipants(Set<InterviewParticipant> acceptedParticipants) {
        this.acceptedParticipants = acceptedParticipants;
    }
}
