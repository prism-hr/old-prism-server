package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDTO;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InstitutionDTO extends ResourceParentDTO {

    @NotNull
    private String currency;

    @NotNull
    private Integer businessYearStartMonth;

    @NotNull
    private BigDecimal minimumWage;

    private Integer importedInstitutionId;

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

    public Integer getImportedInstitutionId() {
        return importedInstitutionId;
    }

    public void setImportedInstitutionId(Integer importedInstitutionId) {
        this.importedInstitutionId = importedInstitutionId;
    }
}
