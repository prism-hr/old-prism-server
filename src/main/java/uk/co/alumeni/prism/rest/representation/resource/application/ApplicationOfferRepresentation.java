package uk.co.alumeni.prism.rest.representation.resource.application;

import org.joda.time.LocalDate;

public class ApplicationOfferRepresentation {

    private String positionName;

    private String positionDescription;

    private LocalDate positionProvisionalStartDate;

    private String appointmentConditions;

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getPositionDescription() {
        return positionDescription;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

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
