package com.zuehlke.pgadmissions.rest.dto.resource;

public class InstitutionDTO extends ResourceParentDTO {

    private String currency;

    private Integer businessYearStartMonth;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getBusinessYearStartMonth() {
        return businessYearStartMonth;
    }

    public void setBusinessYearStartMonth(Integer businessYearStartMonth) {
        this.businessYearStartMonth = businessYearStartMonth;
    }

}
