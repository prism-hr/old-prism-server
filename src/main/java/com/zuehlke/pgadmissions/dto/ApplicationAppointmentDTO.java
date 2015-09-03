package com.zuehlke.pgadmissions.dto;

import java.util.TimeZone;

import org.joda.time.LocalDateTime;

import com.zuehlke.pgadmissions.dto.resource.ResourceStandardDTO;

public class ApplicationAppointmentDTO extends ResourceStandardDTO {

    private String code;
    
    private LocalDateTime interviewDateTime;

    private TimeZone interviewTimeZone;

    private Integer interviewDuration;

    private String interviewLocation;
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
