package com.zuehlke.pgadmissions.rest.representation.resource.application;

public class ApplicationYearRepresentation {

    private String applicationYear;

    private Integer businessYearStartMonth;

    public String getApplicationYear() {
        return applicationYear;
    }

    public void setApplicationYear(String applicationYear) {
        this.applicationYear = applicationYear;
    }

    public Integer getBusinessYearStartMonth() {
        return businessYearStartMonth;
    }

    public void setBusinessYearStartMonth(Integer businessYearStartMonth) {
        this.businessYearStartMonth = businessYearStartMonth;
    }

}
