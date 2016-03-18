package uk.co.alumeni.prism.rest.representation.resource.application;

import java.util.List;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;

public class ApplicationOfferRepresentation {

    private String positionName;

    private String positionDescription;

    private LocalDate positionProvisionalStartDate;

    private String appointmentConditions;
    
    private List<DocumentRepresentation> documents;

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
    
    public List<DocumentRepresentation> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentRepresentation> documents) {
        this.documents = documents;
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
