package uk.co.alumeni.prism.rest.dto.resource;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.api.model.resource.ResourceInstanceDefinition;

public class ResourceStudyOptionInstanceDTO implements ResourceInstanceDefinition {

    private LocalDate applicationStartDate;

    private LocalDate applicationCloseDate;

    private String businessYear;

    private String identifier;

    @Override
    public LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    @Override
    public void setApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    @Override
    public LocalDate getApplicationCloseDate() {
        return applicationCloseDate;
    }

    @Override
    public void setApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    @Override
    public String getBusinessYear() {
        return businessYear;
    }

    @Override
    public void setBusinessYear(String businessYear) {
        this.businessYear = businessYear;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String sequenceIdentifier) {
        this.identifier = sequenceIdentifier;
    }

}
