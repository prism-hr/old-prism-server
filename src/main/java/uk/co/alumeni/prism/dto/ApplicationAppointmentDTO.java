package uk.co.alumeni.prism.dto;

import org.joda.time.LocalDateTime;

import java.util.TimeZone;

public class ApplicationAppointmentDTO extends ResourceFlatToNestedDTO {

    private String applicationCode;

    private LocalDateTime interviewDateTime;

    private TimeZone interviewTimeZone;

    private Integer interviewDuration;

    private String interviewLocation;

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public LocalDateTime getInterviewDateTime() {
        return interviewDateTime;
    }

    public void setInterviewDateTime(LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
    }

    public TimeZone getInterviewTimeZone() {
        return interviewTimeZone;
    }

    public void setInterviewTimeZone(TimeZone interviewTimeZone) {
        this.interviewTimeZone = interviewTimeZone;
    }

    public Integer getInterviewDuration() {
        return interviewDuration;
    }

    public void setInterviewDuration(Integer interviewDuration) {
        this.interviewDuration = interviewDuration;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

}
