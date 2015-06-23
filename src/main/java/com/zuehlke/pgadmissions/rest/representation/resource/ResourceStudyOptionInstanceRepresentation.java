package com.zuehlke.pgadmissions.rest.representation.resource;

import org.joda.time.LocalDate;

public class ResourceStudyOptionInstanceRepresentation {

    private LocalDate applicationStartDate;

    private LocalDate applicationCloseDate;

    private String businessYear;

    private String identifier;

    public LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public LocalDate getApplicationCloseDate() {
        return applicationCloseDate;
    }

    public void setApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    public String getBusinessYear() {
        return businessYear;
    }

    public void setBusinessYear(String businessYear) {
        this.businessYear = businessYear;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
