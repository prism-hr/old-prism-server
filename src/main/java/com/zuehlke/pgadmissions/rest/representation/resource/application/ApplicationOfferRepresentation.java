package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

public class ApplicationOfferRepresentation {

    private String positionName;

    private String positionDescription;

    private LocalDate positionProvisionalStartDate;

    private String appointmentConditions;

    public final String getPositionName() {
        return positionName;
    }

    public final void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public final String getPositionDescription() {
        return positionDescription;
    }

    public final void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

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

    public ApplicationOfferRepresentation withPositionName(String positionName) {
        this.positionName = positionName;
        return this;
    }

    public ApplicationOfferRepresentation withPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
        return this;
    }

    public ApplicationOfferRepresentation withPositionProvisionalStartDate(LocalDate positionProvisionalStartDate) {
        this.positionProvisionalStartDate = positionProvisionalStartDate;
        return this;
    }

    public ApplicationOfferRepresentation withAppointmentConditions(String appointmentConditions) {
        this.appointmentConditions = appointmentConditions;
        return this;
    }

}
