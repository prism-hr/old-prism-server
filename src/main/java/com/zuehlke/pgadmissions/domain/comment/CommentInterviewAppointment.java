package com.zuehlke.pgadmissions.domain.comment;

import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

@Embeddable
public class CommentInterviewAppointment {

    @Column(name = "application_interview_datetime")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime interviewDateTime;

    @Column(name = "application_interview_timezone")
    private TimeZone interviewTimeZone;

    @Column(name = "application_interview_duration")
    private Integer interviewDuration;

    public final LocalDateTime getInterviewDateTime() {
        return interviewDateTime;
    }

    public final void setInterviewDateTime(LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
    }

    public final TimeZone getInterviewTimeZone() {
        return interviewTimeZone;
    }

    public final void setInterviewTimeZone(TimeZone interviewTimeZone) {
        this.interviewTimeZone = interviewTimeZone;
    }

    public final Integer getInterviewDuration() {
        return interviewDuration;
    }

    public final void setInterviewDuration(Integer interviewDuration) {
        this.interviewDuration = interviewDuration;
    }

    public CommentInterviewAppointment withInterviewDateTime(LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
        return this;
    }

    public CommentInterviewAppointment withInterviewTimezone(TimeZone interviewTimeZone) {
        this.interviewTimeZone = interviewTimeZone;
        return this;
    }

    public CommentInterviewAppointment withInterviewDuration(Integer interviewDuration) {
        this.interviewDuration = interviewDuration;
        return this;
    }

}
