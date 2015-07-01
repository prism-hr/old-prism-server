package com.zuehlke.pgadmissions.rest.dto.comment;

import java.util.TimeZone;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDateTime;

public class CommentApplicationInterviewAppointmentDTO {

    private LocalDateTime interviewDateTime;

    @NotNull
    private TimeZone interviewTimeZone;

    @NotNull
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

}
