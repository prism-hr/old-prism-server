package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

public class OfferRepresentation {

    private String positionTitle;
    
    private String positionDescription;
    
    private LocalDate positionProvisionalStartDate;
    
    private String appointmentConditions;

    public final String getPositionTitle() {
        return positionTitle;
    }

    public final void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
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
    
    public OfferRepresentation withPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
        return this;
    }
    
    public OfferRepresentation withPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
        return this;
    }
    
    public OfferRepresentation withPositionProvisionalStartDate(LocalDate positionProvisionalStartDate) {
        this.positionProvisionalStartDate = positionProvisionalStartDate;
        return this;
    }
    
    public OfferRepresentation withAppointmentConditions(String appointmentConditions) {
        this.appointmentConditions = appointmentConditions;
        return this;
    }
    
}
