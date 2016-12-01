package uk.co.alumeni.prism.domain.comment;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class CommentOfferDetail {

    @Column(name = "application_position_provisional_start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate positionProvisionalStartDate;

    @Lob
    @Column(name = "application_appointment_conditions")
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

    public CommentOfferDetail withPositionProvisionStartDate(LocalDate positionProvisionalStartDate) {
        this.positionProvisionalStartDate = positionProvisionalStartDate;
        return this;
    }

    public CommentOfferDetail withAppointmentConditions(String appointmentConditions) {
        this.appointmentConditions = appointmentConditions;
        return this;
    }

}
