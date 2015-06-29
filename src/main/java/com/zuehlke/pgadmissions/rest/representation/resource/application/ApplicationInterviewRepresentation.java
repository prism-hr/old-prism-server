package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;
import java.util.TimeZone;

import com.zuehlke.pgadmissions.rest.representation.comment.CommentAppointmentTimeslotRepresentation;

public class ApplicationInterviewRepresentation {

    private TimeZone interviewTimeZone;

    private Integer interviewDuration;

    private String intervieweeInstructions;

    private String interviewerInstructions;

    private String interviewLocation;

    private List<CommentAppointmentTimeslotRepresentation> appointmentTimeslots;

    private List<ApplicationAppointmentPreferencesRepresentation> appointmentPreferences;

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

    public String getIntervieweeInstructions() {
        return intervieweeInstructions;
    }

    public void setIntervieweeInstructions(String intervieweeInstructions) {
        this.intervieweeInstructions = intervieweeInstructions;
    }

    public String getInterviewerInstructions() {
        return interviewerInstructions;
    }

    public void setInterviewerInstructions(String interviewerInstructions) {
        this.interviewerInstructions = interviewerInstructions;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public List<CommentAppointmentTimeslotRepresentation> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public void setAppointmentTimeslots(List<CommentAppointmentTimeslotRepresentation> appointmentTimeslots) {
        this.appointmentTimeslots = appointmentTimeslots;
    }

    public List<ApplicationAppointmentPreferencesRepresentation> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public void setAppointmentPreferences(List<ApplicationAppointmentPreferencesRepresentation> appointmentPreferences) {
        this.appointmentPreferences = appointmentPreferences;
    }
}
