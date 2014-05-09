package com.zuehlke.pgadmissions.dto;

public class InterviewConfirmDTO {

    private Integer timeslotId;

    private String interviewInstructions;

    private String locationUrl;

    public Integer getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(Integer timeslotId) {
        this.timeslotId = timeslotId;
    }

    public String getInterviewInstructions() {
        return interviewInstructions;
    }

    public void setInterviewInstructions(String interviewInstructions) {
        this.interviewInstructions = interviewInstructions;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

}