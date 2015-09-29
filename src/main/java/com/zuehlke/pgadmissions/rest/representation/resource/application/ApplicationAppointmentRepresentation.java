package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewInstructionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

public class ApplicationAppointmentRepresentation {

    private ResourceRepresentationSimple application;

    private CommentInterviewAppointmentRepresentation appointment;

    private CommentInterviewInstructionRepresentation instruction;

    public ResourceRepresentationSimple getApplication() {
        return application;
    }

    public void setApplication(ResourceRepresentationSimple application) {
        this.application = application;
    }

    public CommentInterviewAppointmentRepresentation getAppointment() {
        return appointment;
    }

    public CommentInterviewInstructionRepresentation getInstruction() {
        return instruction;
    }

    public void setInstruction(CommentInterviewInstructionRepresentation instruction) {
        this.instruction = instruction;
    }

    public void setAppointment(CommentInterviewAppointmentRepresentation appointment) {
        this.appointment = appointment;
    }

    public ApplicationAppointmentRepresentation withApplication(ResourceRepresentationSimple application) {
        this.application = application;
        return this;
    }

    public ApplicationAppointmentRepresentation withAppointment(CommentInterviewAppointmentRepresentation appointment) {
        this.appointment = appointment;
        return this;
    }

    public ApplicationAppointmentRepresentation withInstruction(CommentInterviewInstructionRepresentation instruction) {
        this.instruction = instruction;
        return this;
    }

}
