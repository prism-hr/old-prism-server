package com.zuehlke.pgadmissions.rest.representation.comment;

import org.joda.time.LocalDate;

public class CommentApplicationOfferDetailRepresentation {

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
}
