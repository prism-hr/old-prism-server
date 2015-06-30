package com.zuehlke.pgadmissions.rest.representation.resource;

import java.math.BigDecimal;

public class InstitutionRepresentation extends ResourceParentRepresentation {

    private String currency;

    private BigDecimal minimumWage;

    private Integer businessYearStartMonth;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getMinimumWage() {
        return minimumWage;
    }

    public void setMinimumWage(BigDecimal minimumWage) {
        this.minimumWage = minimumWage;
    }

    public Integer getBusinessYearStartMonth() {
        return businessYearStartMonth;
    }

    public void setBusinessYearStartMonth(Integer businessYearStartMonth) {
        this.businessYearStartMonth = businessYearStartMonth;
    }

}
