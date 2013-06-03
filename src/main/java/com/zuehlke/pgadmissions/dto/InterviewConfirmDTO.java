package com.zuehlke.pgadmissions.dto;


public class InterviewConfirmDTO {

    private Integer timeslotId;

    private String furtherDetails;

    private String furtherInterviewerDetails;

    private String locationUrl;

    public Integer getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(Integer timeslotId) {
        this.timeslotId = timeslotId;
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

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

}