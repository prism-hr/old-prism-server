package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDTO;

public class InstitutionDTO extends ResourceParentDTO {

    @NotNull
    private String currency;

    @NotNull
    private Integer businessYearStartMonth;

    @NotNull
    private BigDecimal minimumWage;

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

    public BigDecimal getMinimumWage() {
        return minimumWage;
    }

    public void setMinimumWage(BigDecimal minimumWage) {
        this.minimumWage = minimumWage;
    }

}
