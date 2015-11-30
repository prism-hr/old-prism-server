package uk.co.alumeni.prism.rest.dto.comment;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

public class CommentOfferDetailDTO {

    @NotNull
    private LocalDate positionProvisionalStartDate;

    private String appointmentConditions;

    public final LocalDate getPositionProvisionalStartDate() {
        return positionProvisionalStartDate;
    }

    public final void setPositionProvisionalStartDate(LocalDate positionProvisionalStartDate) {
        this.positionProvisionalStartDate = positionProvisionalStartDate;
    }

    public final String getAppointmentConditions() {
        return appointmentConditions;
    }

    public final void setAppointmentConditions(String appointmentConditions) {
        this.appointmentConditions = appointmentConditions;
    }
}
