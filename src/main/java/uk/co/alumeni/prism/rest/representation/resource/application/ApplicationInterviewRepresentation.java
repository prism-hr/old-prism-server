package uk.co.alumeni.prism.rest.representation.resource.application;

import java.util.List;

import uk.co.alumeni.prism.rest.representation.comment.CommentAppointmentPreferenceRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentAppointmentTimeslotRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentInterviewInstructionRepresentation;

public class ApplicationInterviewRepresentation {

    private CommentInterviewAppointmentRepresentation interviewAppointment;

    private CommentInterviewInstructionRepresentation interviewInstruction;

    private List<CommentAppointmentTimeslotRepresentation> appointmentTimeslots;

    private List<CommentAppointmentPreferenceRepresentation> appointmentPreferences;

    public CommentInterviewAppointmentRepresentation getInterviewAppointment() {
        return interviewAppointment;
    }

    public void setInterviewAppointment(CommentInterviewAppointmentRepresentation interviewAppointment) {
        this.interviewAppointment = interviewAppointment;
    }

    public CommentInterviewInstructionRepresentation getInterviewInstruction() {
        return interviewInstruction;
    }

    public void setInterviewInstruction(CommentInterviewInstructionRepresentation interviewInstruction) {
        this.interviewInstruction = interviewInstruction;
    }

    public List<CommentAppointmentTimeslotRepresentation> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public void setAppointmentTimeslots(List<CommentAppointmentTimeslotRepresentation> appointmentTimeslots) {
        this.appointmentTimeslots = appointmentTimeslots;
    }

    public List<CommentAppointmentPreferenceRepresentation> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public void setAppointmentPreferences(List<CommentAppointmentPreferenceRepresentation> appointmentPreferences) {
        this.appointmentPreferences = appointmentPreferences;
    }

}
