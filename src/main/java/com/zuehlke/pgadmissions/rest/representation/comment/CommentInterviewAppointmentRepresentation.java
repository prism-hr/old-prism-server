package com.zuehlke.pgadmissions.rest.representation.comment;

import java.util.TimeZone;

import org.joda.time.LocalDateTime;

public class CommentInterviewAppointmentRepresentation {

    private LocalDateTime interviewDateTime;

    private TimeZone interviewTimeZone;

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

    public CommentInterviewAppointmentRepresentation withInterviewDateTime(LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
        return this;
    }
    
    public CommentInterviewAppointmentRepresentation withInterviewTimeZone(TimeZone interviewTimeZone) {
        this.interviewTimeZone = interviewTimeZone;
        return this;
    }
    
    public CommentInterviewAppointmentRepresentation withInterviewDuration(Integer interviewDuration) {
        this.interviewDuration = interviewDuration;
        return this;
    }
    
}
