package uk.co.alumeni.prism.rest.representation.comment;

import org.joda.time.LocalDate;

public class CommentOfferDetailRepresentation {

    private LocalDate positionProvisionalStartDate;

    private String appointmentConditions;

    public LocalDate getPositionProvisionalStartDate() {
        return positionProvisionalStartDate;
    }

    public void setPositionProvisionalStartDate(LocalDate positionProvisionalStartDate) {
        this.positionProvisionalStartDate = positionProvisionalStartDate;
    }

    public String getAppointmentConditions() {
        return appointmentConditions;
    }

    public void setAppointmentConditions(String appointmentConditions) {
        this.appointmentConditions = appointmentConditions;
    }

    public CommentOfferDetailRepresentation withPositionProvisionalStartDate(LocalDate positionProvisionalStartDate) {
        this.positionProvisionalStartDate = positionProvisionalStartDate;
        return this;
    }

    public CommentOfferDetailRepresentation withAppointmentConditions(String appointmentConditions) {
        this.appointmentConditions = appointmentConditions;
        return this;
    }

}
