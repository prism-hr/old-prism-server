package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.IndexColumn;

import com.zuehlke.pgadmissions.domain.enums.InterviewStage;

@Entity(name = "INTERVIEW")
public class Interview implements Serializable {

    private static final long serialVersionUID = -730673777949846236L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "last_notified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastNotified;

    @Column(name = "interview_time")
    private String interviewTime;

    @Column(name = "created_date", insertable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "further_details")
    private String furtherDetails;

    @Column(name = "further_interviewer_details")
    private String furtherInterviewerDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_id")
    private ApplicationForm application;

    @Column(name = "location_url")
    private String locationURL;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date interviewDueDate;

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "interview_id")
    private List<Interviewer> interviewers = new ArrayList<Interviewer>();

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private InterviewStage stage = InterviewStage.INITIAL;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "time_zone", nullable = false)
    private TimeZone timeZone = TimeZone.getTimeZone("GMT");

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "interview_id")
    @IndexColumn(name = "participant_position")
    private List<InterviewParticipant> participants = new ArrayList<InterviewParticipant>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "interview_id")
    @IndexColumn(name = "timeslot_position")
    private List<InterviewTimeslot> timeslots = new ArrayList<InterviewTimeslot>();

    @Transient
    private String timeHours;

    @Transient
    private String timeMinutes;

    @Transient
    private Boolean takenPlace = false;

    public Date getLastNotified() {
        return lastNotified;
    }

    public void setLastNotified(Date lastNotified) {
        this.lastNotified = lastNotified;
    }

    public String getFurtherDetails() {
        return furtherDetails;
    }

    public void setFurtherDetails(String furtherDetails) {
        this.furtherDetails = furtherDetails;
    }

    public String getFurtherInterviewerDetails() {
        return furtherInterviewerDetails;
    }

    public void setFurtherInterviewerDetails(String furtherInterviewerDetails) {
        this.furtherInterviewerDetails = furtherInterviewerDetails;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public String getLocationURL() {
        return locationURL;
    }

    public void setLocationURL(String locationURL) {
        this.locationURL = locationURL;
    }

    public Date getInterviewDueDate() {
        return interviewDueDate;
    }

    public void setInterviewDueDate(Date dueDate) {
        this.interviewDueDate = dueDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public List<Interviewer> getInterviewers() {
        return interviewers;
    }

    public void setInterviewers(List<Interviewer> interviewers) {
        this.interviewers.clear();
        for (Interviewer interviewer : interviewers) {
            if (interviewer != null) {
                this.interviewers.add(interviewer);
            }
        }
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date created) {
        this.createdDate = created;
    }

    public String getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(String interviewTime) {
        this.interviewTime = interviewTime;
        if (interviewTime != null) {
            int semiColonPosition = interviewTime.indexOf(':');
            timeHours = interviewTime.substring(0, semiColonPosition);
            timeMinutes = interviewTime.substring(semiColonPosition + 1);
        }
    }

    public InterviewStage getStage() {
        return stage;
    }

    public void setStage(InterviewStage stage) {
        this.stage = stage;
    }

    public String getTimeHours() {
        return timeHours;
    }

    public String getTimeMinutes() {
        return timeMinutes;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public List<InterviewParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<InterviewParticipant> participants) {
        this.participants = participants;
    }

    public List<InterviewTimeslot> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<InterviewTimeslot> timeslots) {
        this.timeslots = timeslots;
    }

    public Boolean getTakenPlace() {
        return takenPlace;
    }

    public void setTakenPlace(Boolean takenPlace) {
        this.takenPlace = takenPlace;
    }

    public boolean isScheduled() {
        return getStage() == InterviewStage.SCHEDULED;
    }

    public boolean isScheduling() {
        return getStage() == InterviewStage.SCHEDULING;
    }

    public boolean isParticipant(RegisteredUser user) {
        return getParticipant(user) != null;
    }

    public InterviewParticipant getParticipant(RegisteredUser user) {
        for (InterviewParticipant participant : getParticipants()) {
            if (participant.getUser().getId().equals(user.getId())) {
                return participant;
            }
        }
        return null;
    }

    public boolean hasAllInterviewersProvidedFeedback() {
        for (Interviewer interviewer : getInterviewers()) {
            if (interviewer.getInterviewComment() == null) {
                return false;
            }
        }
        return true;
    }

    public boolean isDateExpired() {
        if (interviewDueDate == null) {
            return false;
        }
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        return !getInterviewDueDate().after(today);
    }

}
