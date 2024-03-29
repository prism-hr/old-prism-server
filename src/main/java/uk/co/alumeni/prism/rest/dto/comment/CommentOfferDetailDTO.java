package uk.co.alumeni.prism.rest.dto.comment;

import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;

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
