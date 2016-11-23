package uk.co.alumeni.prism.rest.dto.comment;

import org.joda.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import java.util.TimeZone;

public class CommentInterviewAppointmentDTO {

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
